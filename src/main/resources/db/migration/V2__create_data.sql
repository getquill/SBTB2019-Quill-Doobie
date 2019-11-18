--
-- Data for Name: address; Type: TABLE DATA; Schema: public; Owner: sa
--

INSERT INTO address VALUES (50, 'Summer St', 11111, 'NY');
INSERT INTO address VALUES (51, 'Winter St', 11111, 'NY');
INSERT INTO address VALUES (52, 'Super St', 11111, 'WI');


--
-- Data for Name: permission; Type: TABLE DATA; Schema: public; Owner: sa
--

INSERT INTO permission VALUES (1, 'Dancing');
INSERT INTO permission VALUES (2, 'Drinking');
INSERT INTO permission VALUES (3, 'Impaling');
INSERT INTO permission VALUES (4, 'Assassinating');
INSERT INTO permission VALUES (5, 'Cleaning');
INSERT INTO permission VALUES (6, 'Name Calling');


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: sa
--

INSERT INTO person VALUES (1, 'Jack', 'Ripper', 55);
INSERT INTO person VALUES (2, 'Vlad', 'Dracul', 321);
INSERT INTO person VALUES (3, 'Joe', 'Bloggs', 20);


--
-- Data for Name: robot; Type: TABLE DATA; Schema: public; Owner: sa
--

INSERT INTO robot VALUES (100, 'Terminator', 300);
INSERT INTO robot VALUES (101, 'Wall-E', 250);


--
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: sa
--

INSERT INTO role VALUES (1, 'Partygoer');
INSERT INTO role VALUES (2, 'Drinker');
INSERT INTO role VALUES (3, 'Assassin');
INSERT INTO role VALUES (4, 'Impaler');
INSERT INTO role VALUES (5, 'Custodian');
INSERT INTO role VALUES (6, 'Loudmouth');


--
-- Data for Name: role_permission; Type: TABLE DATA; Schema: public; Owner: sa
--

INSERT INTO role_permission VALUES (2222, 1, 1);
INSERT INTO role_permission VALUES (2223, 2, 2);
INSERT INTO role_permission VALUES (2224, 3, 3);
INSERT INTO role_permission VALUES (2225, 3, 4);
INSERT INTO role_permission VALUES (2226, 4, 4);
INSERT INTO role_permission VALUES (2227, 5, 5);
INSERT INTO role_permission VALUES (2228, 6, 6);


--
-- Data for Name: troll; Type: TABLE DATA; Schema: public; Owner: sa
--

INSERT INTO troll VALUES (50, 'Spammmalot', 'Headachemaker', 23);
INSERT INTO troll VALUES (51, 'Flabbergasto', 'Poopoomouth', 24);
INSERT INTO troll VALUES (52, 'Aggrevatus', 'Keybreaker', 24);


--
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: sa
--

INSERT INTO user_role VALUES (1111, 1, 1);
INSERT INTO user_role VALUES (1112, 1, 3);
INSERT INTO user_role VALUES (1113, 2, 1);
INSERT INTO user_role VALUES (1114, 2, 2);
INSERT INTO user_role VALUES (1111, 2, 4);
INSERT INTO user_role VALUES (1115, 3, 1);
INSERT INTO user_role VALUES (1116, 50, 1);
INSERT INTO user_role VALUES (1117, 50, 2);
INSERT INTO user_role VALUES (1117, 50, 6);
INSERT INTO user_role VALUES (1118, 51, 1);
INSERT INTO user_role VALUES (1117, 51, 2);
INSERT INTO user_role VALUES (1119, 51, 6);
INSERT INTO user_role VALUES (1120, 52, 1);
INSERT INTO user_role VALUES (1117, 52, 2);
INSERT INTO user_role VALUES (1121, 52, 6);
INSERT INTO user_role VALUES (1122, 100, 1);
INSERT INTO user_role VALUES (1123, 100, 2);
INSERT INTO user_role VALUES (1124, 100, 3);
INSERT INTO user_role VALUES (1125, 101, 5);
