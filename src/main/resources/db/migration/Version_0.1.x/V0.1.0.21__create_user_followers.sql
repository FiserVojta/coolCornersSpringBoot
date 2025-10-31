create table corneruser_followers (
                                user_id bigint not null,
                                follower_id bigint not null,
                                primary key (user_id, follower_id),
                                constraint fk_user_followers_user
                                    foreign key (user_id) references corneruser(id),
                                constraint fk_user_friends_follower
                                    foreign key (follower_id) references corneruser(id)
);