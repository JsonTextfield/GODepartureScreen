# Open API Data Catalogue

**Version Date:** 2018-07-11  
**Information & Information Technology**

---

## 1. Introduction

### 1.1 Open API Data Catalogue Overview
This document provides central descriptions of business data such as standards definition of data elements, their meanings, and allowable values for the Open API attributes. A key is required for access, which determines accessible topics.

**Topics covered:**
*   Stop Service
*   ServiceataGlance
*   ServiceUpdate
*   Schedule
*   Fares Service
*   GTFS Feeds (VehiclePosition, TripUpdate, and Service Alerts)

### 1.2 The Key Elements
Below are the most common elements included in this document:

| # | Element Name | Description |
|---|---|---|
| 1 | Attribute Name | Unique identifier of an attribute |
| 2 | Description | Short description of an attribute |
| 3 | Data Type | Defines allowed data format or storage range |
| 4 | Required Field? | Indicates if information is mandatory or optional |
| 5 | Maximum Length | Maximum characters allowed for text areas |
| 6 | Notes | Additional information about an attribute |

---

## 2. Data Catalogue

### 2.1 Common Data Elements
These standard elements are returned in the `Metadata` structure of every service call.

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| TimeStamp | Date and time the information was returned | datetime | | Mandatory | Format: YYYY-MM-DD hh:mm:ss |
| ErrorCode | Identifier for the error message | varchar | 10 | Mandatory | See Error Codes table below |
| ErrorMessage | Text of the error message | varchar | 50 | Mandatory | See Error Messages table below |

#### Error Codes and Messages
| Code | Message | Description |
|---|---|---|
| 200 | OK | No error |
| 204 | No Content | No data was found |
| 400 | Bad Request | Malformed client request |
| 401 | Unauthorized | Key missing or incorrect |
| 403 | Forbidden | Key does not have access to the topic |
| 404 | Not Found | URI invalid or resource does not exist |
| 410 | Gone | API version no longer exists |
| 429 | Too Many Requests | Key's use limit reached |
| 500 | Internal Server Error | Issue on server; post on developer forums |
| 503 | Service Unavailable | Servers down or overloaded |
| 504 | Gateway Timeout | Server did not respond |

---

### 2.2 Stop Service
Returns predictions and details for scheduled service stops.

#### 2.2.1 StopNextService
Gives predictions for all lines (rail and bus) feeding a certain stop.
*   **Parameters:** `StopCode` (The stop identifier)

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| StopCode | Unique station code | varchar | 10 | Mandatory | Example: 00129 |
| LineCode | 2-char corridor (rail) or 2-digit route (bus) | varchar | 10 | Mandatory | Example: 21 |
| LineName | Name of the rail corridor or bus route | varchar | 100 | Optional | Example: "Milton Train-Bus Service" |
| ServiceType | Indicates train or bus | char | 1 | Mandatory | T = Train, B = Bus |
| DirectionCode | Line code for the route | varchar | 10 | Mandatory | Bus variants use LineCode + Letter (e.g., 21N) |
| DirectionName | Direction and final destination | varchar | 100 | Optional | Example: "21N – Milton GO" |
| TripOrder | Sequential order of the trip | int | | Optional | Example: 1 |
| TripNumber | Unique alpha-numeric trip code | varchar | 50 | Mandatory | Example: 21253 |
| UpdateTime | Last modification timestamp | datetime | | Optional | Format: YYYY-MM-DD ddThh:mm:ss.nnn |
| Status | Delay status of the trip | varchar | 10 | Optional | S = Stopped, M = Moving |
| Latitude | Vehicle current latitude | numeric | | Optional | Example: 43.5692670 |
| Longitude | Vehicle current longitude | numeric | | Optional | Example: -79.6688830 |
| ScheduledDepartureTime | Recorded scheduled departure | datetime | | Optional | Format: YYYY-MM-DDThh:mm:ss |
| ComputedDepartureTime | Last computed departure (incl. delays) | datetime | | Optional | Format: YYYY-MM-DDThh:mm:ss |
| DepartureStatus | Stop departure delay status | char | 1 | Optional | E = Estimated, C = Cancelled, A = Actual |
| ScheduledPlatform | Scheduled train platform/track | varchar | 5 | Optional | Platform for Union, Track for Line stations |
| ActualPlatform | Actual train platform/track | varchar | 5 | Optional | Populated if status is Actual (A) |

#### 2.2.2 StopDetails
Displays location details, facilities, and parking for a given stop.
*   **Parameters:** `StopCode`

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| ZoneCode | Fare zone code | varchar | 50 | Optional | Example: 22 |
| StreetNumber | Station street number | varchar | 10 | Optional | Example: 6845 |
| Intersection | Nearest main intersection | varchar | 255 | Optional | Example: Millcreek Dr & Aquitaine Ave |
| City | City where station is located | varchar | 50 | Optional | Example: Mississauga |
| StreetName | Name of the street | varchar | 255 | Optional | Example: Millcreek Drive |
| Code | Unique Station Code | varchar | 10 | Optional | Example: 00129 |
| StopName | Name of the station | varchar | 255 | Optional | Example: Meadowvale GO |
| StopNameFr | French translation of stop name | varchar | 255 | Optional | |
| IsBus | Indicates if bus station | bit | | Mandatory | true/false |
| IsTrain | Indicates if train station | bit | | Mandatory | true/false |
| Longitude | Geographic longitude | varchar | 50 | Optional | Example: -79.754517 |
| Latitude | Geographic latitude | varchar | 50 | Optional | Example: 43.597499 |
| DrivingDirections | Directions to the location | varchar | 255 | Optional | |
| DrivingDirectionsFr | French driving directions | varchar | 255 | Optional | |
| BoardingInfo | Boarding information | nvarchar | 255 | Optional | |
| BoardingInfoFr | French boarding info | nvarchar | 255 | Optional | |
| TicketSales | Ticket sales description | varchar | 255 | Optional | |
| TicketSalesFr | French ticket sales info | nvarchar | 255 | Optional | |

##### 2.2.2.1 StopDetails – Facility
| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Facility type code | varchar | 10 | Mandatory | ABM, BR (Bike Rack), EV (Elevator), TVM, etc. |
| Description | Facility description | varchar | 50 | Optional | Example: "Debit cards accepted" |
| DescriptionFr | French description | nvarchar | 50 | Optional | |

##### 2.2.2.2 StopDetails – Parking
| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Name | Parking lot name | varchar | 1024 | Optional | |
| NameFr | French parking lot name | nvarchar | 1024 | Optional | |
| ParkSpots | Number of parking spots | int | | Optional | |
| Type | Parking lot type | varchar | 100 | Optional | Regular or Structure |

##### 2.2.2.3 StopDetails – Place
Geographical area including additional stops.

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique place code | varchar | 50 | Mandatory | Example: "medvis" |
| Name | Place name | varchar | 255 | Optional | Based on GTFS values |
| Longitude | Longitude coordinate | decimal | | Optional | |
| Latitude | Latitude coordinate | decimal | | Optional | |
| Radius | Search radius in meters | int | | Optional | Default 0, no upper limit |

##### 2.2.2.4 StopDetails – Stop
Additional stops within a place.

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique station code | varchar | 50 | Mandatory | Example: "ME" |
| Name | Station name | varchar | 255 | Optional | Example: "Meadowvale GO" |

#### 2.2.3 StopDestinations
Destinations from origin at specific time to final stop.
*   **Parameters:** `StopCode`, `FromTime` (e.g. 0800), `ToTime` (e.g. 1300)

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique Station Code | varchar | 50 | Optional | Example: 00129 |
| Name | Name of the station | varchar | 255 | Optional | Example: "Meadowvale GO" |

##### 2.2.3.1 StopDestinations – Line

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique line/route code | varchar | 10 | Mandatory | Example: 21 |
| Display | Route and destination name | varchar | 500 | Optional | |
| Direction | Direction of travel | char | 1 | Mandatory | e.g. E |
| DestinationStop| Name of final destination | varchar | 255 | Optional | Example: USBT |

#### 2.2.4 StopAll
Retrieve all stop locations and descriptions. (Excludes waypoints, yards, and garages).

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| LocationCode | Station identification code | varchar | 50 | Mandatory | Example: 00001 |
| LocationName | Station name | varchar | 255 | Mandatory | Example: "Newmarket GO" |
| LocationType | Stop type description | varchar | 255 | Mandatory | BS, BT, CL, GT, PK, ST, etc. |
| PublicStopID | 6-digit unique identifier | varchar | 50 | Mandatory for Bus | Used for next-bus arrival queries |

---

### 2.3 Service Update
Returns messages for specified message classes. Includes Service Alerts, Information Alerts, and Marketing Alerts.

#### 2.3.1 ServiceAlertAll
Displays current service alerts by date.

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique message identifier | varchar | 50 | Mandatory | Example: M0000163040 |
| Category | Service category | varchar | 100 | Optional | Example: "Service Disruption" |
| SubCategory | Service subcategory | varchar | 100 | Optional | Example: "Train Service Suspension" |
| ParentCode | Related previous message | varchar | 50 | Optional | |
| Status | Message status | varchar | 10 | Optional | INIT, CORR, FINAL, UPD |
| PostedDateTime | Date/time initially posted | varchar | 50 | Optional | Format: yyyy-mm-ddThh:mm:ss.nnn |
| SubjectEnglish | English subject text | varchar | 255 | Optional | |
| SubjectFrench | French subject text | varchar | 255 | Optional | |
| BodyEnglish | English body text | varchar | MAX | Optional | |
| BodyFrench | French body text | varchar | MAX | Optional | |

##### 2.3.1.1 ServiceAlertAll – Line

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique line identifier | varchar | 10 | Mandatory | LW, LE, GT, ST, RH, BI, MI |

##### 2.3.1.2 ServiceAlertAll – Stops

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Name | Station name | varchar | 255 | Mandatory | |
| Code | Unique stop identifier | varchar | 50 | Mandatory | Example: MR |

##### 2.3.1.3 ServiceAlertAll – Trips

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Number | Unique trip code | varchar | 50 | Mandatory | Example: 806 |

#### 2.3.2 InformationAlertAll
Current informational messages. Structure same as 2.3.1.

#### 2.3.3 MarketingAlertAll
Current marketing alert messages. Structure same as 2.3.1.

#### 2.3.4 UnionDepartureAll
Nearest departures for buses and trains from Union Station.

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Info | Trip information | varchar | 255 | Optional | Example: "Bus Terminal" |
| TripNumber | Unique trip identifier | varchar | 10 | Mandatory | Example: 00001 |
| Platform | Station platform number | varchar | 25 | Optional | |
| Service | Line name | varchar | 50 | Optional | |
| ServiceType | Service type code | char | 1 | Mandatory | T = Train, B = Bus |
| Time | Last updated timestamp | datetime | | Mandatory | Format: yyyy-mm-ddThh:mm:ss |

##### 2.3.4.1 UnionDeparturesAll – Stops

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Name | Stop name | varchar | 255 | Mandatory | |
| Code | Unique station code | varchar | 50 | Mandatory | |

#### 2.3.5 ServiceGuarantee
Indicates if service guarantee (fare refund) applies for a trip.
*   **Parameters:** `OperationalDay` (yyyymmdd), `tripNumber`

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Departure station code | varchar | 25 | Mandatory | |
| Scope | Fare guarantee status | varchar | 25 | Optional | Y = In effect, N = No guarantee |
| ReasonEn | English reason | varchar | MAX | Optional | |
| ReasonFr | French reason | varchar | MAX | Optional | |

#### 2.3.6 ExceptionsTrain
Exceptions to advertised train schedules.

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| TripNumber | Unique trip code | nvarchar | 10 | Mandatory | Example: E158D |
| TripName | Name of the trip | nvarchar | 255 | Optional | |
| IsCancelled | Cancellation flag | bit | | Mandatory | 1 = True, 0 = False |
| IsOverride | Override flag | bit | | Mandatory | 1 = True, 0 = False |

##### 2.3.6.1 ExceptionsTrain – Stop

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Order | Sequential order | int | | Mandatory | Example: 1 |
| ID | Unique station ID | int | | Mandatory | Example: 907 |
| SchArrival | Scheduled arrival | nvarchar | 50 | Optional | |
| SchDeparture | Scheduled departure | nvarchar | 50 | Optional | |
| Name | Station name | nvarchar | 255 | Optional | |
| IsStopping | Is trip stopping? | bit | | Mandatory | |
| IsCancelled | Is stop cancelled? | bit | | Mandatory | |
| IsOverride | Is stop override? | bit | | Mandatory | |
| Code | Unique station code | nvarchar | 50 | Optional | Example: UN |
| ActualTime | Actual arrival/dep time | nvarchar | 50 | Optional | |
| ServiceType | Train or Bus | char | 1 | Mandatory | T = Train |

#### 2.3.7 ExceptionsBus
Exceptions to advertised bus schedules. Structure same as 2.3.6.

#### 2.3.8 ExceptionAll
Exceptions for both trains and buses. Structure same as 2.3.6.

---

### 2.4 Service At Glance
Returns all currently in-service trips.

#### 2.4.1 ServiceAtaGlance – BusesAll (Trips)

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| BusType | Double Decker or Coach | varchar | 25 | Mandatory | |
| TripNumber | Trip identifier | varchar | 10 | Mandatory | |
| StartTime | Scheduled departure | varchar | 5 | Mandatory | Format: hh:mm |
| EndTime | Scheduled arrival | varchar | 5 | Mandatory | Format: hh:mm |
| LineCode | Bus line/route code | varchar | 2 | Optional | Example: 12 |
| RouteNumber | Line or variance code | varchar | 2 | Optional | Letter indicates change (12B) |
| VariantDir | Direction of variant | varchar | 5 | Optional | N, S, E, W |
| Display | Route and destination | varchar | 128 | Optional | |
| Latitude | Current latitude | float | | Mandatory | |
| Longitude | Current longitude | float | | Mandatory | |
| IsInMotion | Is moving? | bit | | Mandatory | true/false |
| DelaySeconds | Expected delay | int | | Mandatory | Negative = ahead |
| Course | Trip direction | numeric | | Mandatory | |
| FirstStopCode | Starting stop ID | varchar | 10 | Optional | |
| LastStopCode | Destination stop ID | varchar | 10 | Optional | |
| PrevStopCode | Last station stopped | varchar | 10 | Optional | |
| NextStopCode | Next scheduled station | varchar | 10 | Optional | |
| AtStationCode | Station currently at | varchar | 10 | Optional | |
| ModifiedDate | Last update timestamp | dateTime | | Mandatory | Format: yyyy-mm-ddThh:mm:ss.nnn |

#### 2.4.2 ServiceAtaGlance – TrainsAll (Trips)

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Cars | Number of cars | varchar | 10 | Mandatory | 6, 10, or 12 |
| TripNumber | Trip identifier | varchar | 10 | Mandatory | |
| StartTime | Scheduled departure | varchar | 5 | Mandatory | |
| EndTime | Scheduled arrival | varchar | 5 | Mandatory | |
| LineCode | Train line code | varchar | 2 | Optional | |
| RouteNumber | Line code for trip | varchar | 2 | Optional | |
| VariantDir | Variant direction | varchar | 5 | Optional | |
| Display | Destination name | varchar | 128 | Optional | |
| Latitude | Latitude | float | | Mandatory | |
| Longitude | Longitude | float | | Mandatory | |
| IsInMotion | Is moving? | bit | | Mandatory | |
| DelaySeconds | Delay in seconds | int | | Mandatory | |
| Course | Trip direction | numeric | | Mandatory | |
| FirstStopCode | Start stop code | varchar | 10 | Optional | |
| LastStopCode | End stop code | varchar | 10 | Optional | |
| PrevStopCode | Prev stop code | varchar | 10 | Optional | |
| NextStopCode | Next stop code | varchar | 10 | Optional | |
| AtStationCode | Current station code | varchar | 10 | Optional | |
| ModifiedDate | Update timestamp | dateTime | | Mandatory | |

---

### 2.5 Schedule

#### 2.5.1 ScheduleJourney
Provides journey details including rail/bus routes, trips, stops, and transfers.
*   **Parameters:** `date` (yyyy-mm-dd), `fromStopCode`, `toStopCode`, `startTime` (hhmm), `maxJourney`

##### 2.5.1.1 ScheduleJourney - Journey

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| From | Departure Station Code | varchar | 50 | Mandatory | |
| To | Arrival Station Code | varchar | 50 | Mandatory | |
| Time | Start time | Time | | Mandatory | hh:mm:ss |
| Date | Effective date | Date | | Mandatory | yyyy-mm-dd |

##### 2.5.1.2 ScheduleJourney – Services

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Colour | Line service color hex | varchar | 50 | Optional | Rail only |
| Type | Service Type | char | 1 | Mandatory | R = Rail, B = Bus, RB = Both |
| Direction | Service direction | char | 1 | Mandatory | N, S, E, W |
| Code | Unique service code | varchar | 10 | Mandatory | |
| StartTime | Departure time | datetime | | Mandatory | |
| EndTime | Arrival time | datetime | | Mandatory | |
| Duration | Total duration | datetime | | Mandatory | hh:mm:ss |
| Accessible | Accessibility code | varchar | 5 | Mandatory | R, B, or RB accessible |
| StartSortTime | Departure sort time | varchar | 10 | Mandatory | |
| EndSortTime | Arrival sort time | varchar | 10 | Mandatory | |
| TripHash | Real-time query hash | varchar | 10 | Mandatory | |
| TransferCount | Max transfer count | varchar | 10 | Optional | |

##### 2.5.1.3 ScheduleJourney – Trips

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Number | Unique trip identifier | varchar | 10 | Mandatory | |
| Display | Route and destination | varchar | 128 | Optional | |
| Line | Code of the line | varchar | 50 | Optional | |
| Direction | Direction of travel | char | 1 | Mandatory | |
| LineVariant | Line variant info | varchar | 10 | Optional | |
| Type | Service Type | char | 1 | Mandatory | T = Train |

##### 2.5.1.4 ScheduleJourney – Stops

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique stop code | varchar | 50 | Mandatory | |
| Order | Sequence order | int | | Mandatory | |
| Time | Scheduled time | datetime | | Mandatory | hh:mm:ss |
| isMajor | Is major transfer point? | bit | | Mandatory | |
| SortingTime | Sort definition | datetime | | Mandatory | |
| DestinationStopCode | Trip destination code | varchar | 10 | Mandatory | |
| DepartFromCode | Departure stop code | varchar | 10 | Mandatory | |
| TripPatternID | Unique pattern ID | int | | Mandatory | |

##### 2.5.1.5 ScheduleJourney – Transfers

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Time | Transfer start time | dateTime | | Mandatory | |
| Code | Transfer stop code | varchar | 10 | Mandatory | |
| Order | Transfer sequence | int | | Mandatory | |

##### 2.5.1.6 ScheduleJourney – TransferLinks

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| ToStopCode | Next stop after transfer | varchar | 10 | Mandatory | |
| ToTrip | Next trip ID | varchar | 10 | Mandatory | |
| FromStopCode | Stop prior to transfer | varchar | 10 | Mandatory | |
| FromTrip | Trip ending prior | varchar | 10 | Mandatory | |
| TransferDuration| Time between trips | Time | | Optional | mm:ss |

#### 2.5.2 ScheduleJourneyToStopCode
Similar to `ScheduleJourney` but queries by origin stop only.

#### 2.5.3 ScheduleLine
Line details by date, line code, and direction.
*   **Parameters:** `date` (yyyymmdd), `lineCode`, `lineDirection`

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Corridor/Route code | varchar | 2 | Optional | LW, 16, etc. |
| Direction | Line direction | char | 1 | Mandatory | N, S, E, W |
| Type | Train (T) or Bus (B) | char | 1 | Mandatory | |

##### 2.5.3.1 ScheduleLine – Trip

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Number | Unique trip identifier | varchar | 50 | Mandatory | |
| Display | Route and destination | varchar | 128 | Optional | |

##### 2.5.3.2 ScheduleLine – Stops

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique stop code | varchar | 50 | Mandatory | |
| Order | Stopping sequence | int | | Mandatory | |
| Time | Scheduled arrival | datetime | | Mandatory | |
| SortingTime | Departure sorting | datetime | | Mandatory | |

#### 2.5.4 ScheduleLineAll
Lines in effect for provided date.
*   **Parameters:** `date` (yyyymmdd)

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Name | Line name | varchar | 100 | Optional | |
| Code | Unique line identifier | varchar | 10 | Mandatory | |
| IsBus | Bus trip flag | bit | | Mandatory | |
| IsTrain | Train trip flag | bit | | Mandatory | |

##### 2.5.4.1 ScheduleLineAll – Variant

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Variant route code | varchar | 10 | Mandatory | Example: 12A |
| Display | Destination name | varchar | 128 | Optional | |
| Direction | Travel direction | char | 1 | Mandatory | |

#### 2.5.5 ScheduleLineStop
Line stops in effect for Date, Line Code, and Direction.

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Line code | varchar | 10 | Mandatory | |
| Direction | Direction | char | 1 | Mandatory | N, S, E, W, IN, OUT |
| Display | Route/Destination | varchar | 128 | Optional | |

##### 2.5.5.1 ScheduleLineStop – Stop

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Code | Unique stop code | varchar | 50 | Mandatory | |
| Order | Stopping sequence | int | 50 | Mandatory | |
| Name | Station name | varchar | 255 | Optional | |
| Type | Service Type | char | 1 | Mandatory | R, B, or RB |
| isMajor | Major transfer point? | bit | | Mandatory | |

#### 2.5.6 ScheduleTrip
Information for a specific scheduled trip.

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Number | Unique trip code | varchar | 50 | Mandatory | |
| Destination | Trip destination code | nvarchar | 50 | Mandatory | |
| Longitude | Longitude | numeric | | Optional | |
| Latitude | Latitude | numeric | | Optional | |
| Status | Trip delay status | char | 1 | Optional | S = Stop, M = Moving |
| TimeStamp | Trip time | datetime | | Mandatory | |

---

### 2.6 Fares Service
Displays all fares between two stations.

#### 2.6.1 FareCategory
*   **Parameters:** `fromStationCode`, `toStationCode`, `OperationalDay` (Optional)

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Type | Fare category type | varchar | 10 | Mandatory | Adult, Student, Senior, Child, Group |

##### 2.6.1.1 FareCategory – Ticket

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Type | Ticket medium | varchar | 10 | Mandatory | Paper, Presto |

##### 2.6.1.2 FareCategory – Fare

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Type | Specific fare type | varchar | 50 | Mandatory | Single Ride, Day Pass, etc. |
| Amount | Paid amount | numeric | | Mandatory | |
| Category | Fare category | varchar | 50 | Mandatory | Normal, Discount |

---

### 2.7 GTFS Feeds
Realtime information (GTFS Realtime) for vehicle positions and trip updates.

#### 2.7.1 The Header

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Gtfs_version | Feed version | varchar | 50 | Mandatory | Currently "2.0" |
| Incrementality | Update type | varchar | 50 | Mandatory | Full_DataSet or Differential |
| Timestamp | Generation time | datetime | | Mandatory | Unix timestamp |

#### 2.7.2 GTFS Entity

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| id | Unique identifier | varchar | 10 | Mandatory | |
| is_deleted | Deletion flag | bit | | Optional | |
| trip_update | Trip delay data | object | | Conditional | |
| vehicle | Position data | object | | Conditional | |
| alert | Alert data | object | | Conditional | |

#### 2.7.3 GTFS VehiclePosition

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Stop_id | Current stop ID | char | 10 | Optional | |
| Current_Status | Status relative to stop | varchar | 10 | Optional | |
| Timestamp | Position measurement | datetime | | Mandatory | |
| Congestion_Level| Traffic level | varchar | 500 | Optional | |
| Occupancy_Status| Passenger occupancy | varchar | 10 | Optional | |

##### 2.7.3.1 GTFS Vehicle - Trip

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| trip_id | GTFS trip ID | varchar | 10 | Mandatory | |
| route_id | GTFS route ID | char | 5 | Mandatory | |
| direction_id | Direction | char | 1 | Mandatory | |
| start_time | Scheduled start time | datetime | | Mandatory | hh:mm:ss |
| start_date | Scheduled start date | datetime | | Mandatory | YYYYMMDD |
| schedule_rel | Schedule relationship | varchar | 10 | Optional | SCHEDULED |

##### 2.7.3.2 GTFS Vehicle - Vehicle

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| id | Internal vehicle ID | char | 5 | Mandatory | Do not show to users |
| label | Visible identification | varchar | 128 | Optional | Show this to users |
| License_Plate | Vehicle license plate | varchar | 10 | Optional | |

##### 2.7.3.3 GTFS Vehicle - Position

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Latitude | Vehicle latitude | numeric | | Mandatory | |
| Longitude | Vehicle longitude | numeric | | Mandatory | |
| Bearing | Bearing in degrees | char | 1 | Mandatory | |
| Odometer | Odometer value | numeric | | Optional | |
| Speed | Speed in m/s | Numeric | | Mandatory | |

#### 2.7.4 GTFS TripUpdate

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| timestamp | Prediction time | datetime | | Mandatory | |
| delay | Vehicle delay (sec) | int | | Optional | Negative = ahead |

##### 2.7.4.3 GTFS TripUpdate – Stop_Time_Update

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| Stop_id | Unique stop ID | Char | 10 | Mandatory | |
| Arrival | Predicted arrival event | datetime2| | Mandatory | |
| Schedule_rel | Relationship to static | varchar | 10 | Mandatory | Scheduled, Added, etc. |

#### 2.7.5 GTFS Alerts
##### 2.7.5.1 GTFS Alerts – Entity

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| id | Unique identifier | int | | Mandatory | |
| cause | Alert cause | varchar | 255 | Optional | |
| effect | Alert effect | varchar | 255 | Optional | |

##### 2.7.5.2 GTFS Alerts – ActivePeriod

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| start | Active start time | datetime | | Mandatory | |
| end | Active end time | datetime | | Mandatory | |

##### 2.7.5.3 GTFS Alerts – InformedEntity

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| agency_id | Affected agency | char | 2 | Mandatory | |
| route_id | Affected route | char | 1 | Mandatory | |
| stop_id | Affected stop | char | 10 | Mandatory | |

##### 2.7.5.4 GTFS Alerts – Url/Translation

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| text | URL string | varchar | 1000 | Optional | |
| language | Translation language | varchar | 1000 | Optional | "en" or "fr" |

##### 2.7.5.5 GTFS Alerts – HeaderText/Translation

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| text | Alert summary header | varchar | 1000 | Optional | |
| language | Language | varchar | 1000 | Optional | |

##### 2.7.5.6 GTFS Alerts – DescriptionText/Translation

| Attribute Name | Description | Data Type | Max Length | Required? | Notes |
|---|---|---|---|---|---|
| text | Full alert description | varchar | 1000 | Optional | |
| language | Language | varchar | 1000 | Optional | |
