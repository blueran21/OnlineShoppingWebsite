CONSISTENCY LOCAL_ONE;

CREATE KEYSPACE IF NOT EXISTS orders_keyspace
    WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 1}
     AND durable_writes = true;

CREATE TYPE IF NOT EXISTS orders_keyspace.order_item (
    itemid     text,
    quantity   int,
    unitprice  double
);

CREATE TABLE IF NOT EXISTS orders_keyspace.orders (
    order_id     uuid PRIMARY KEY,
    user_id      text,
    items        frozen<list<frozen<orders_keyspace.order_item>>>,
    total_price  double,
    status       text,
    created_at   timestamp,
    updated_at   timestamp
);
