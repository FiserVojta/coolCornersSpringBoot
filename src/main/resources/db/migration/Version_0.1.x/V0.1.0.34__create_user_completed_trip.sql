create table if not exists corneruser_completed_trip (
    trip_id bigint not null,
    user_id bigint not null,
    primary key (trip_id, user_id),
    constraint fk_completed_trip_trip
        foreign key (trip_id) references trip(id),
    constraint fk_completed_trip_user
        foreign key (user_id) references corneruser(id)
);
