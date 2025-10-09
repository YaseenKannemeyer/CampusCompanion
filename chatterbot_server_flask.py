# chatterbot_server_flask.py
from flask import Flask, request, jsonify
from chatterbot import ChatBot
from chatterbot.trainers import ListTrainer, ChatterBotCorpusTrainer
import json
import os

app = Flask(__name__)

# ============ Initialize ChatBot ============
bot = ChatBot(
    'StudentBot',
    storage_adapter='chatterbot.storage.SQLStorageAdapter',
    database_uri='sqlite:///chatbot.sqlite3',
    logic_adapters=[
        {
            'import_path': 'chatterbot.logic.BestMatch',
            'default_response': "I’m not sure about that. Can you rephrase?",
            'maximum_similarity_threshold': 0.65  # approximate match
        }
    ],
    read_only=False
)

# ============ JSON TRAINER ============
json_trainer = ListTrainer(bot)
json_path = os.path.join(os.path.dirname(__file__), 'knowledge.json')

if os.path.exists(json_path):
    with open(json_path, 'r', encoding='utf-8') as f:
        knowledge = json.load(f)
        for entry in knowledge:
            training_sequence = []

            # Use questionList if available
            if 'questionList' in entry and entry['questionList']:
                training_sequence.extend(entry['questionList'])
            # Fallback to single question
            elif 'question' in entry and entry['question']:
                training_sequence.append(entry['question'])

            # Add keywords
            if 'keywords' in entry and entry['keywords']:
                training_sequence.extend(entry['keywords'])

            # Add answer
            if 'answer' in entry and entry['answer']:
                training_sequence.append(entry['answer'])

            # Only train if there’s something
            if training_sequence:
                json_trainer.train(training_sequence)

# ============ CORPUS TRAINER ============
corpus_trainer = ChatterBotCorpusTrainer(bot)
corpus_trainer.train(
    "chatterbot.corpus.english.greetings",
    "chatterbot.corpus.english.conversations",
    "chatterbot.corpus.english.humor",
    #"chatterbot.corpus.english.compliment"
)

# ============ API ENDPOINT ============
@app.route("/ask", methods=["POST"])
def ask():
    try:
        data = request.json
        user_message = data.get("message", "").strip()
        if not user_message:
            return jsonify({"response": "Please send a message."})

        # Get bot response
        response = str(bot.get_response(user_message))

        # Optional: dynamic learning for new questions
        if response.lower().startswith("i’m not sure") and user_message:
            json_trainer.train([user_message, response])

        return jsonify({"response": response})

    except Exception as e:
        return jsonify({"response": f"⚠️ Error: {str(e)}"})

# ============ RUN SERVER ============
if __name__ == "__main__":
    app.run(host="127.0.0.1", port=5000)
