import socket
import argparse
import random


from flask import Flask, request, jsonify
from datetime import datetime
import os
import requests
import json
import re
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('./quizapp-e1d08-firebase-adminsdk-13ek2-96cd715707.json')
firebase_admin.initialize_app(cred)
db = firestore.client()

app = Flask(__name__)

def process_quiz(quiz_text):
    questions = []
    print("Raw Quiz Text:", quiz_text)
    pattern = re.compile(
        r'QUESTION\s*\d*:\s*(.*?)\s+'
        r'OPTION A:\s*(.*?)\s+'
        r'OPTION B:\s*(.*?)\s+'
        r'OPTION C:\s*(.*?)\s+'
        r'OPTION D:\s*(.*?)\s+'
        r'ANS:\s*(.*?)(?=\s*QUESTION|$)',
        re.DOTALL
    )

    matches = pattern.findall(quiz_text)
    print("Matches Found:", matches)

    for question, option_a, option_b, option_c, option_d, correct_ans in matches:
        question_data = {
            "question": question.strip(),
            "options": [option_a.strip(), option_b.strip(), option_c.strip(), option_d.strip()],
            "correct_answer": correct_ans.strip()
        }
        questions.append(question_data)

    return questions

def fetchQuizFromChatGPT(student_topic):
    headers = {
        'Authorization': 'Bearer',
        'Content-Type': 'application/json'
    }
    unique_identifier = random.randint(1, 1000000)
    data = {
        "model": "gpt-3.5-turbo",
        "messages": [
            {"role": "system", "content": f"Quiz session {unique_identifier}: Generate a very creative quiz."},
            {"role": "user", "content": f"Generate a quiz with 3 creative and unique questions to test students on the topic '{student_topic}'. For each question, generate 4 options where only one of the options is correct. Format the response as follows: QUESTION: [Your question here]? OPTION A: [First option] OPTION B: [Second option] OPTION C: [Third option] OPTION D: [Fourth option] ANS: [Only the content of the answer, be sure to exclude the option letter] Ensure text is properly formatted. It needs to start with a question, then the options, and finally the correct answer. Follow this pattern for all questions."}
        ],
        "temperature": 0.9,
        "max_tokens": 800
    }
    response = requests.post("https://api.openai.com/v1/chat/completions", headers=headers, json=data)
    if response.status_code == 200:
        return response.json()['choices'][0]['message']['content']
    else:
        print(f"Error in API call: {response.status_code} {response.text}")
        return None


def upload_quiz_to_firestore(topic_name, image_url, quiz_data):
    topics = db.collection('topics').where('topicName', '==', topic_name).get()

    if topics:
        topic_ref = topics[0].reference
    else:
        topic_ref = db.collection('topics').document()
        topic_ref.set({
            'topicId': topic_ref.id,
            'topicName': topic_name,
            'topicImage': image_url
        })

    questions_ref = topic_ref.collection('questions')

    for question_doc in questions_ref.get():
        question_doc.reference.delete()

    for i, question in enumerate(quiz_data, start=1):
        question_ref = questions_ref.document(f'question{i}')
        question_ref.set({
            'question': question['question'],
            'option1': question['options'][0],
            'option2': question['options'][1],
            'option3': question['options'][2],
            'option4': question['options'][3],
            'answer': question['correct_answer']
        })

    return topic_ref.id


@app.route('/')
def index():
    return "Welcome to the Flask API!"

@app.route('/generateQuiz', methods=['GET'])
def generate_quiz():
    topic = request.args.get('topic')
    app.logger.debug(f"Received topic: {topic}")
    if not topic:
        app.logger.error("No topic provided")
        return jsonify({'error': 'Missing topic parameter'}), 400

    quiz = fetchQuizFromChatGPT(topic)
    if quiz is None:
        app.logger.error("Failed to fetch quiz from external API")
        return jsonify({'error': 'Failed to fetch quiz from API'}), 500

    quiz_data = process_quiz(quiz)
    topic_id = upload_quiz_to_firestore(topic, "url_to_image", quiz_data)
    if topic_id:
        app.logger.info("Quiz uploaded successfully")
        return jsonify({'message': 'Quiz generated and uploaded successfully', 'topic_id': topic_id}), 200
    else:
        app.logger.error("Failed to upload quiz to Firestore")
        return jsonify({'error': 'Failed to upload quiz'}), 500




if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--name', default=f"Chatgpt_{datetime.now().strftime('%Y%m%d_%H%M%S')}_{socket.gethostname()}",
                        help='Specify a name')
    args = parser.parse_args()

    port_num = 5000
    app.run(port=port_num, debug=True)

