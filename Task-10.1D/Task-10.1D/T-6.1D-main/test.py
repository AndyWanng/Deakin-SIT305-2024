import unittest
from main import app

class FlaskAppTests(unittest.TestCase):
    def setUp(self):
        self.app = app.test_client()
        self.app.testing = True

    def test_get_quiz_and_upload_success(self):
        response = self.app.get('/generateQuiz?topic=Music')
        self.assertEqual(response.status_code, 200)
        quiz_data = response.get_json()
        self.assertIn('message', quiz_data)
        self.assertIn('topic_id', quiz_data)
        self.assertEqual(quiz_data['message'], 'Quiz generated and uploaded successfully')

    def test_get_quiz_failure_missing_topic(self):
        response = self.app.get('/generateQuiz')
        self.assertEqual(response.status_code, 400)


if __name__ == '__main__':
    unittest.main()
