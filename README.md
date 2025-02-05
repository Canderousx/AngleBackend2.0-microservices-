### **Important: This version of Angle Backend is under active development and heavy refactoring. It is not finished and may undergo significant changes at any time.**

This project is the newest iteration of Angle Backend (see: https://github.com/Canderousx/AngleBackend) and it is compatible with the newest version of Angle Frontend (see: https://github.com/Canderousx/AngleFrontend2.0).


## What's new?

- The backend architecture has been restructured into a microservices-based approach.
- Apache Kafka as a message broker
- WebSocket integration for real-time user notifications and tracking user activity, including video views.
- Redis-based caching mechanism for improved performance.


### Stay tuned for updates:)



# 1. Architecture

This backend is built using a microservices architecture with Spring Boot and Spring Cloud Gateway (*Security-Gateway* service).
Each microservice has its own MySQL and Redis database. Microservices communicate asynchronously using Apache Kafka for event-driven messaging and RESTful APIs for synchronous interactions.

Below is a detailed description of each microservice in this project:

- [Security-Gateway](#security-gateway)
- [Auth-Service](#auth-service)
- [Notification-Service](#notification-service)
- [Video-Manager](#video-manager)
- [Video-Processor](#video-processor)
- [Thumbnail-Generaotr](#thumbnail-generator)
- [Comments-Manager](#comments-manager)
- [Mail-Service](#mail-service)
- [Report-Service](#report-service)
- [Stats-Service](#stats-service)

## *Security-Gateway*

Built with Spring Cloud Gateway, this service acts as the entry point for all external requests, routing them to the appropriate microservices.
It is also responsible for CORS configuration across the entire backend.


## *Auth-Service*

Developed with Spring Boot, this service handles user authentication, registration, and JWT (JSON Web Token) generation, including refresh tokens, ensuring continuous authentication while using the platform.
Each JWT contains user-specific data: the user ID is stored as the subject, and the IP address is included in the token's claims.
This allows other microservices — without direct access to the accounts database — to recognize and verify users based on the provided JWT.

Additionally, Auth-Service is responsible for generating account-related notifications, which are then sent through Kafka to the **Notification-Service**.


## *Notification-Service*

Developed with Spring Boot, this service is responsible for sending real-time notifications to users via WebSocket and storing them in its database.

It listens to notification-related topics on Kafka, processing each event by saving it to the database and delivering it to the user through WebSocket


## *Video-Manager*

Developed with Spring Boot, this service is responsible for storing metadata of all uploaded videos.
It also acts as the source of the *video_uploaded* event.

When a user uploads a new video, this service handles the storage of the raw *.mp4* file and then publishes its metadata via **Apache Kafka**.
This event-driven approach enables the **Video-Processor** and **Thumbnail-Generator** services to initiate their respective tasks.

Additionally, Video-Manager generates video-related notifications, such as when video processing is completed, which are then sent via Kafka to the **Notification-Service**.


It also listens to video-related topics on **Kafka**, enabling it to process and handle videos dynamically based on the received events.


## *Video-Processor*

Developed with Spring Boot, this service is responsible for converting raw .mp4 files into m3u8 playlists using **FFmpeg**.

This conversion enables adaptive streaming, allowing users to switch video quality dynamically and download content in segments.

The service listens to the *video_uploaded* topic on Kafka, processing each event by converting the corresponding .mp4 file.
Once the conversion is complete, all metadata related to the generated playlist is sent back through Kafka to the **Video-Manager**.


## *Thumbnail-Generator*

Developed with Spring Boot, this service extracts frames from *.mp4* files using **FFmpeg**.
These frames serve as potential thumbnails for the uploaded video, allowing users to select their preferred option.

The service listens to the *video_uploaded* topic on **Kafka**, processing each event by extracting frames from the corresponding *.mp4* file.
Once the process is complete, all metadata related to the generated thumbnails is sent back via Kafka to the Video-Manager.


## *Comments-Manager*

Developed with Spring Boot, this service is responsible for managing video comments, including storing new ones.

It also listens to comment-related and video-related topics on **Kafka**, enabling it to process and handle comments dynamically based on the received events.

## *Mail-Service*

Developed with Spring Boot, this service is responsible for sending emails to users.

It listens to mail-related topics on **Kafka**, processing each event to deliver the corresponding email notifications.

## *Report-Service*

Developed with Spring Boot, this service is responsible for managing and storing report data.
Users can submit reports regarding content that potentially violates platform rules.

Each report can be reviewed by an admin, and based on the verdict, different **Kafka** events are triggered to the appropriate services for further processing.


## *Stats-Service*

Developed with Spring Boot, this service is responsible for real-time user tracking using **WebSocket** connections.
It monitors video views, their duration, and user interactions, such as pauses, likes, and dislikes.

For fast processing, tracking data is initially stored in **Redis**.
Every 5 minutes, a scheduled task persists completed view sessions to the database.

After updating the views, the service sends a **Kafka** event, which is consumed by *Video-Manager* to update the video's total view count.










