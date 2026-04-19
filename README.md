# Mini-SideKiq

[![Java](https://img.shields.io/badge/Java-17-orange)]
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)]

## Overview

A Miniature SideKiq , Using Redis As In-memory Database
with safety features Like Safety Queue and Multithreading 

---
## Features 
* **Reliable Queueing:** Uses the Redis `RPOPLPUSH` atomic operation to prevent data loss. Jobs are moved to a "Safety Net" list during processing.
* **Self-Healing Janitor:** A background reaper service that monitors "stuck" jobs and re-queues them automatically if a worker crashes.
* **Tiered State Management:** Tracks job lifecycles through `Pending`, `Running`, `Completed`, and `Failed` states using Redis Hashes.
* **Resource Isolation:** Dedicated `ThreadPoolTaskExecutor` to prevent worker loops from starving the main application threads.
* **Dead Man's Switch:** Implemented TTL-based memory reclamation to ensure Redis storage remains optimized.
## Api

```json
POST http://localhost:8080/pushwork
        
Content-Type: application/json

{
  "WorkName": "Task",
  "WorkTask": "Email"
}

```
Returns a Job Id and Print Which Thread Completed Task,
Info of work is stored in redis which you can retrieve With a Get Api according to need

```json
56d657f2-9715-4177-ae23-e7e84581149f // job Id (String)
Worker-2 processed 56d657f2-9715-4177-ae23-e7e84581149f
```

## Prerequisites 
1. Java 17
2. Redis 
3. SpringBoot

## Setup

Run Redis on you Linux Terminal or Any O.S
```bash
// for Linux ( i used WSL2 you can use Same command)
sudo apt install redis-server
sudo server redis-server start
```
Run your Application
```Java
gradlew.bat bootRun ; // windows
```