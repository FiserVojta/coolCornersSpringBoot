create table corneruser_friends (
                                user_id bigint not null,
                                friend_id bigint not null,
                                primary key (user_id, friend_id),
                                constraint fk_user_friends_user
                                    foreign key (user_id) references corneruser(id),
                                constraint fk_user_friends_friend
                                    foreign key (friend_id) references corneruser(id)
);