# AquaConnect

AquaConnect is a water tanker booking web application built using Java Spring Boot backend services and PostgreSQL database connectivity.

This project helps users find nearby water suppliers and book water tankers during water shortage situations or book tankers for any special occasions.

Owners can register their water supply business, add branches, vehicles, drivers, water quantity available with them , and pricing details based on distance and quantity user need.

Users can search nearby water suppliers, view reservoir images, compare prices, book water tankers (just like Rapido), track delivery updates, and give feedback after delivery or report issues to owners.
---

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL

### Tools
- IntelliJ IDEA
- Postman
- pgAdmin
- GitHub

---

## Main Actors

### Owner
- Registers water supply business
- Adds branches
- Adds vehicles
- Adds drivers
- Updates water quantity
- Sets water prices

### Driver
- Receives delivery orders
- Delivers water tanker
- Updates delivery status

### User
- Registers and logs in
- Searches nearby water suppliers
- Books water tankers
- Gives feedback after delivery or report an issue to owner

---

## Current Features

### Day 1-3 Owner Registration Module

The owner will register with:
- Full name
- Phone number
- Email
- Password
- Bank account details
- Branch details
- Water quantity
- Vehicles
- Drivers
- Water prices
- Reservoir images


### Day 4-5 User Authentication & Authorization

- User entity creation
- USER, OWNER, ADMIN role management
- User registration
- User login
- JWT authentication
- JWT token generation and validation
- Spring Security integration
- Role-based authorization
- Reservoir listing API
- Reservoir details API
- Global exception handling
- Invalid credentials handling
- Branch repository implementation

### Day 6-7 Driver Authentication

- Driver login implemented
- DRIVER role added
- Driver credentials generated automatically
- JWT authentication for drivers
- Multi-driver registration support
- Added multiple branches and owners

### Day 8-9 Nearby Reservoir & Booking Module

- Nearby reservoir search API
- Distance calculation using latitude and longitude
- Radius-based reservoir filtering
- Reservoir image support
- Available drivers and vehicles count
- Starting water price display
- Booking module implementation
- Water quantity validation
- Water quantity deduction after booking
- Booking status management
- Owner notification message generation
- Booking API tested successfully

### Day 10 Updated Tanker booking

- Booking entity and repository implementation
- Create booking API
- Branch-wise booking management
- Owner pending bookings API
- Driver and vehicle assignment
- Booking status tracking
- Driver assigned bookings API
- Delivery status updates
- Automatic driver and vehicle availability management

---
