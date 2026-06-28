from fastapi import FastAPI, UploadFile, File
import whisper
import tempfile
import os

app = FastAPI()
model = whisper.load_model("tiny")

@app.post("/speech-to-text")
async def speech_to_text(file: UploadFile = File(...)):
    temp_filename = None

    try:
      
        with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as temp:
            temp.write(await file.read())
            temp_filename = temp.name

        result = model.transcribe(temp_filename, language="vi")

        text = result["text"].strip().lower()

        return {
            "success": True,
            "text": text
        }

    except Exception as e:
        return {
            "success": False,
            "error": str(e)
        }

    finally:
       
        if temp_filename and os.path.exists(temp_filename):
            os.remove(temp_filename)