# n8n Docker Environment Setup Guide

This document explains how to configure, prepare, and run the **n8n** automation platform using Docker Compose.  
It covers environment setup, folder permissions, and common troubleshooting steps.

---

## 1. Project structure

Your project directory should look like this:

```
n8n-project/
├── docker-compose.yml
├── setup
├── .env
└── README.md
```

---

## 2. Environment setup

Before starting the container, you must prepare environment variables and local folders.

### Run the setup script
```bash
chmod +x setup
./setup
```

This script will:

- Detect your local **user ID (UID)** and **group ID (GID)**.
- Export them as environment variables `N8N_UID` and `N8N_GID`.
- Create or update a `.env` file with those variables.
- Create the folders:
  ```
  	mkdir -p ~/.n8n/input-files
	mkdir -p ~/.n8n/output-files
  ```
- Apply proper permissions so the Docker container can write to them.
  ```
  	sudo chown -R $USER:$USER ~/.n8n
	chmod -R 700 ~/.n8n
  ```

---

## 3. The .env file

Docker Compose automatically loads environment variables from a file named `.env` in the same folder as `docker-compose.yml`.

Example of what your `.env` file will contain:

```env
N8N_UID=1000
N8N_GID=1000
```

> You do **not** need to edit this file manually — it’s managed by the `setup` script.

---

## 4. Docker Compose configuration

Your `docker-compose.yml` should look like this:

```yaml
version: '3.8'

services:
  n8n:
    image: n8nio/n8n
    restart: always
    ports:
      - "5678:5678"
    environment:
      - GENERIC_TIMEZONE=America/Sao_Paulo
      - GENERIC_LANGUAGE=pt-BR
    user: "${N8N_UID}:${N8N_GID}"
    volumes:
      - ~/.n8n:/home/node/.n8n
    security_opt:
      - apparmor=unconfined
    entrypoint: >
      sh -c "mkdir -p /home/node/.n8n/input-files /home/node/.n8n/output-files &&
             chown -R ${N8N_UID}:${N8N_GID} /home/node/.n8n &&
             exec docker-entrypoint.sh n8n"
```

---

## 5. Start the container

Once your setup script has run successfully, bring up the n8n container:

```bash
docker compose up -d
```

Verify that the container is running:
```bash
docker ps
```

You should see something like:

```
CONTAINER ID   IMAGE       STATUS         PORTS                    NAMES
a1b2c3d4e5f6   n8nio/n8n   Up 2 minutes   0.0.0.0:5678->5678/tcp   n8n-n8n-1
```

Access n8n in your browser:
```
http://localhost:5678
```

---

## 6. Troubleshooting

### Permission denied on startup
If you see:
```
mkdir: can't create directory '/home/node/.n8n/output-files': Permission denied
```
Run:
```bash
sudo chown -R 1000:1000 ~/.n8n
chmod -R 755 ~/.n8n
docker compose down -v
docker compose up -d
```

### “Operation not permitted” with .XIM-unix or systemd-private-*
This means you accidentally mounted `/tmp` instead of your n8n data folder.  
Fix your volume mapping in `docker-compose.yml`:
```yaml
volumes:
  - ~/.n8n:/home/node/.n8n
```
Then remove the unwanted files:
```bash
sudo rm -rf ~/.n8n/.XIM-unix ~/.n8n/systemd-private-*
```

---

## 7. Reset and clean

To completely reset everything:

```bash
docker compose down -v
sudo rm -rf ~/.n8n/input-files ~/.n8n/output-files
./setup
docker compose up -d
```

---

## 8. Summary

| Step | Action |
|------|--------|
| 1 | Run `./setup` to generate `.env` and folders |
| 2 | Check `.env` contains `N8N_UID` and `N8N_GID` |
| 3 | Start container with `docker compose up -d` |
| 4 | Access n8n at [http://localhost:5678](http://localhost:5678) |
| 5 | Fix permissions using `chown` if any errors occur |

---

**Author:** Your Name  
**Date:** 2025-10-18  
**Description:** Local n8n container setup guide for Ubuntu/Linux.
