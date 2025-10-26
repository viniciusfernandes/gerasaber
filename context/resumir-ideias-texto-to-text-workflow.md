# n8n + ChatGPT Workflow (Revised Context — Plain Text Response)

## Objective
Create an **n8n workflow** that automates receiving uploaded files via webhook, processing them with ChatGPT, and sending the processed **text summary** back to a REST endpoint — without saving any local files.

---

## Workflow Description

### 1. Webhook Trigger Node
- Exposes an **HTTP endpoint** to receive uploaded files.  
- Accepts multiple file types: `.txt`, `.docx`, `.pdf`.

### 2. ChatGPT Processing Node
- Extracts text content from each uploaded file.  
- Sends the content to **ChatGPT** using the OpenAI API.  
- ChatGPT must:
  - Summarize the document content.  
  - Format the summary as a **bullet list** in plain text.  
  - Return only the **plain text summary** (no encoding, no base64, no file generation).

### 3. REST Delivery Node
- Sends the summarized **plain text** back to the REST endpoint via a **POST** request:
  ```
  http://localhost:8088/material-didatico/
  ```
- Includes the text summary inside the JSON body under a key named `"summary"`.

---

## Expected Workflow Structure
1. **Webhook Trigger Node** → Receives uploaded files.  
2. **File Extractor Node (Optional)** → Converts binary to text if necessary.  
3. **ChatGPT Summarization Node** → Sends the extracted text to OpenAI and receives a **plain text summary**.  
4. **REST Delivery Node** → Sends the text summary to the configured endpoint.

---

## Output Requirements
- Provide a single `.json` file importable directly into **n8n**.  
- Define all nodes, parameters, and connections to automate the process end-to-end.  
- ChatGPT’s response must be **plain text only**, formatted as a bullet list.  
- The workflow must operate entirely in memory (no files written to disk).
