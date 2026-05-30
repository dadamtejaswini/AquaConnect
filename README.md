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


### Day 11-12 User Dashboard Module

* User dashboard implementation
* Display nearby reservoirs on map
* User location integration
* Reservoir listing with details
* Water quantity selection
* Booking form integration

### Day 13-15 Owner Dashboard Module

* Owner dashboard implementation
* Branch-wise booking management
* Pending bookings view
* Driver assignment interface
* Vehicle assignment interface
* Booking status monitoring

### Day 16-18 Driver Dashboard Module

* Driver dashboard implementation
* Assigned bookings view
* Customer details display
* Delivery address display
* Delivery status update functionality
* Driver availability management

### Day 19-20 Live Tracking & Maps

* Leaflet map integration
* User location marker
* Reservoir location markers
* Delivery route visualization
* Booking location mapping
* Distance-based reservoir search improvements

### Day 20-22 Frontend Authentication

* React login page implementation
* React registration page implementation
* JWT token storage
* Protected routes configuration
* Role-based dashboard navigation
* Logout functionality

### Day 23-25 UI & User Experience Improvements

* Responsive dashboard layouts
* Improved booking cards
* Reservoir image display
* Water quantity selection improvements
* Navigation bar enhancements
* Professional UI styling

### Day 26 Booking Tracking & Notifications

* Booking tracking page
* Driver assignment notifications
* Booking progress updates
* Delivery completion workflow
* User booking history
* Owner booking history

### Day 27 Final Integration & Testing

* Backend and frontend integration
* API testing and validation
* Database relationship verification
* Bug fixes and performance improvements
* Project documentation updates
* GitHub repository maintenance
* Final project testing and deployment preparation

---
