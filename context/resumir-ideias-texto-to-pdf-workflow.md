# n8n + ChatGPT Workflow (Revised Context)

## Objective
Create an **n8n workflow** that automates receiving uploaded files via webhook, processing them with ChatGPT, and sending the processed result back to a REST endpoint — without saving any local files.

---

## Workflow Description

### 1. Webhook Trigger Node
- Exposes an HTTP endpoint to receive uploaded files.  
- Accepts multiple file types: `.txt`, `.docx`, `.pdf`.

### 2. ChatGPT Processing Node
- Extracts text content from each uploaded file.  
- Sends the content to **ChatGPT** using the OpenAI API.  
- ChatGPT must:
  - Summarize the file content.  
  - Format the result as a **bullet list**.  
  - Return a **.pdf** file as a base64-encoded binary response.

### 3. REST Delivery Node
- Sends the processed PDF back to the REST endpoint via **POST** request:  
  ```
  http://localhost:8088/material-didatico/
  ```
- Includes the summarized PDF (file or base64 string) in the request body.

---

## Expected Workflow Structure
1. **Webhook Trigger Node** → Receives uploaded files.  
2. **File Extractor Node (Optional)** → Converts binary to text if necessary.  
3. **ChatGPT Summarization Node** → Sends text to OpenAI and receives summarized PDF.  
4. **REST Delivery Node** → Sends the processed PDF to the endpoint.

---

## Output Requirements
- Provide a single `.json` file importable into n8n.  
- Define all nodes, parameters, and connections automatically.  
- Avoid saving files to disk — the process runs fully in memory.
