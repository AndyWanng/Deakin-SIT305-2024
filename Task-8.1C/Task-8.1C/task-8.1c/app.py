from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

chatgpt_api_url = "https://api.openai.com/v1/chat/completions"
chatgpt_api_key = ""


@app.route("/chat", methods=["POST"])
def chat():
    try:
        data = request.json

        headers = {
            "Authorization": f"Bearer {chatgpt_api_key}",
            "Content-Type": "application/json"
        }
        response = requests.post(chatgpt_api_url, json=data, headers=headers)

        return jsonify(response.json()), response.status_code

    except Exception as e:
        return str(e), 500


if __name__ == "__main__":
    app.run(debug=True)

