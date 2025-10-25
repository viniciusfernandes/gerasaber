# n8n Workflow Creation Context

## Objective
Create an **n8n workflow** that automates the processing, summarization, and distribution of uploaded files.

## Workflow Description

### 1. File Reception Node
- Must expose an **HTTP endpoint** capable of receiving multiple files:
  - Plain text (`.txt`)
  - Microsoft Word (`.docx`)
  - PDF (`.pdf`)
- All received files must be stored under:
  ```
  ~/.n8n/input-files
  ```

### 2. Summarization Node
- Must take the uploaded files from the `input-files` directory.
- For each file, send its contents to **ChatGPT**.
- ChatGPT should:
  - Summarize the document content.
  - Format the summary as a **bullet list**.
  - Return a **.pdf** file named according to the document’s content.
- The generated summarized PDFs must be stored in:
  ```
  ~/.n8n/output-files
  ```

### 3. REST Delivery Node
- After generating the summarized `.pdf` files, the workflow must send them via a **POST** request to the REST endpoint:
  ```
  http://localhost:8088/material-didatico/
  ```

## Output Requirements
- Provide a single **`.json` file** that can be **imported directly into n8n**.
- The JSON must define all nodes and connections necessary to build this workflow automatically.

## Expected Workflow Structure
1. **Webhook Trigger** → receives uploaded files.
2. **File Storage Node** → saves received files in `~/.n8n/input-files`.
3. **AI Summarization Node** → sends content to ChatGPT and formats results.
4. **File Writer Node** → saves bullet-style summaries as `.pdf` in `~/.n8n/output-files`.
5. **HTTP Request Node** → sends generated PDFs to the REST endpoint.
