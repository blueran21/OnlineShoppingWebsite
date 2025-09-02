# Online Shopping System – Architecture, User Flow & Data Flow (Final)

This document summarizes the final design of the three-service project and shows how users and data move through the system.

## Stack Overview

* **Services**: `account-service` (JWT auth, MySQL), `order-service` (orders, Kafka events, Cassandra), `item-service` (catalog & inventory, MongoDB), `payment-mock` (WireMock).
* **Infra**: Kafka + Zookeeper, Cassandra, MySQL, MongoDB (all via Docker).
* **Security**: JWT (HS256 in dev), `Authorization: Bearer <token>` between client → services.
* **Ports (host → container)**: account **8084→8084**, order **8082→8082**, item **8081→8081**, payment-mock **8083→8080**, MySQL **3307→3306**, MongoDB **27018→27017**, Cassandra **9042→9042**, Kafka **9092 (external)**/**29092 (internal)**, ZK **2181**.

---

## 1) Architecture (Containers & Dependencies)

```mermaid
flowchart LR
  U["User / Browser<br/>Swagger UI / cURL"]

  subgraph NET [Docker Network]
    subgraph ACC [account service :8084]
      ACCAPI[/REST: /auth, /accounts/]
      ACCDB[(MySQL<br/>accounts_db)]
    end

    subgraph ORD [order service :8082]
      ORDAPI[/REST: /orders/]
      ORDDB[(Cassandra<br/>orders_keyspace)]
      ORDPROD[[Kafka Producer]]
    end

    subgraph ITM [item service :8081]
      ITMAPI[/REST: /items, /inventory/]
      ITMDB[(MongoDB<br/>itemdb)]
    end

    subgraph KAFKA [Kafka Cluster]
      ZK[Zookeeper :2181]
      K[Kafka Broker<br/>internal 29092 / external 9092]
    end

    PAY["payment-mock WireMock<br/>8083 host to 8080 container"]
  end

  %% Edges (client → services)
  U -->|Issue or Login| ACCAPI
  U -->|Authenticated API - Bearer JWT| ORDAPI
  U -->|Optional item browsing| ITMAPI

  %% Service-to-DB
  ACCAPI <-->|JPA| ACCDB
  ITMAPI <-->|Spring Data Mongo| ITMDB
  ORDAPI <-->|Spring Data Cassandra| ORDDB

  %% Service-to-service flows
  ORDAPI -->|price / decrement / increment| ITMAPI
  ORDAPI -->|submit payment| PAY
  ORDPROD -. publish .-> K

```

**Notes**

* `order-service` requires a valid **JWT** (issued by `account-service`) for its endpoints.
* `item-service` is called by `order-service` for pricing and inventory mutation.
* Payment is mocked by WireMock; success/failure is controlled by stub rules.
* `order-service` also publishes `order-events` to Kafka for downstream consumers (future).

---

## 2) User Flow – Login & Authenticated Read

```mermaid
sequenceDiagram
    autonumber
    participant B as Browser / Client
    participant ACC as account-service
    participant MYSQL as MySQL

    B->>ACC: POST /auth/login { email, password }
    ACC->>MYSQL: findByEmail(email)
    MYSQL-->>ACC: account + passwordHash
    ACC-->>B: 200 { token: JWT(iss, sub=userId, exp, aud, jti, ... ) }

    Note over B: Store token (dev: Swagger memory / curl var)<br/>Use in Authorization header

    B->>ACC: GET /accounts/{id}\nAuthorization: Bearer <JWT>
    ACC-->>B: 200 { account dto }
```

**Key points**

* JWT is signed by `account-service`. In prod, use HTTPS and consider RS256 + JWKS.
* Token include claims such as `iss`, `sub` (userId), `exp`, and optionally `aud`, `jti`, `ver`.

---

## 3) User Flow – Create Order (Success + Compensation)

```mermaid
sequenceDiagram
    autonumber
    participant B as Browser / Client
    participant ORD as order-service
    participant ITM as item-service
    participant MONGO as MongoDB
    participant PAY as payment-mock
    participant CASS as Cassandra
    participant K as Kafka (order-events)

    rect rgb(245,245,245)
      B->>ORD: POST /orders { userId, items[] }\nAuthorization: Bearer <JWT>
      ORD->>ITM: GET /items/{id} (for each line) – price lookup
      ITM->>MONGO: find item
      MONGO-->>ITM: item + price
      ITM-->>ORD: item dto(s)

      loop for each item
        ORD->>ITM: POST /inventory/{id}/decrement { qty }
        ITM->>MONGO: atomic dec if enough
        MONGO-->>ITM: ok/insufficient
        ITM-->>ORD: 200 or 409
      end

      alt any decrement failed
        ORD-->>B: 409 Conflict (Insufficient inventory)
      else all decremented
        ORD->>CASS: INSERT order (status=CREATED)
        CASS-->>ORD: ack
        ORD-->>K: publish OrderEvent(CREATED)
        ORD->>PAY: POST /payments { orderId, userId, amount }

        alt payment SUCCESS
          ORD->>CASS: UPDATE status=PAID
          CASS-->>ORD: ack
          ORD-->>B: 201 Created (OrderResponse: PAID)
        else payment FAILED
          par compensate
            loop each item
              ORD->>ITM: POST /inventory/{id}/increment { qty }
            end
          and cancel
            ORD->>CASS: UPDATE status=CANCELLED
          end
          ORD-->>B: 201 Created (OrderResponse: CANCELLED)
        end
      end
    end
```

**Notes**

* Pricing uses item snapshots at order time.
* Inventory decrement is atomic in Mongo (query with `gte` + `$inc`).
* On failure, partial decrements are **compensated** by increments.
* Payment is mocked: amount `!= 999` → SUCCESS; amount `== 999` → FAILED.

---

## 4) Data Flow (at rest)

* **MySQL (accounts\_db)**: `accounts` (id, email, password\_hash, name, created\_at, updated\_at, \[token\_version]).
* **MongoDB (itemdb)**: `items` (id, name, upc, price, pictures…), `inventory` (itemId, quantity).
* **Cassandra (orders\_keyspace)**: `orders` table; `order_item` UDT (itemId, quantity, unitPrice). Order rows have status lifecycle: CREATED → PAID → (COMPLETED|CANCELLED).
* **Kafka**: `order-events` (OrderEvent with orderId, userId, items\[], total, status).

---

```
```
