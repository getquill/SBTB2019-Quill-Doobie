# Quill + Doobie = Better Together Examples
Examples used in the Scale By the Bay talk by Alexander Ioffe.

This repo includes the various examples used in the presentation as well as the simple rest service 
presented at the end using, [quill](https://getquill.io/), [doobie](http://tpolecat.github.io/doobie/), 
[http4s](http://http4s.org/), [fs2](), and [circe](https://github.com/circe/circe). It is based on
[todo-http4s-doobie](https://github.com/jaspervz/todo-http4s-doobie) by @jaspervz please have a look
over there for more details on how the library is put together.



## End points
All of the endpoints are GETs which result in a single `SELECT` query.
Since these endpoints are for demonstration purposes some use Doobie and Quill and some only use Quill.

Examples:
 - `simple/quill/peopleNamed/Joe` - People Named Joe
 - `/quill/peopleWhoAre/Partygoer/named/Joe` - All Partygoer Humans named Joe
 - `/quill/trollsWhoAre/Partygoer/in/NY` - All Partygoer Trolls in New York
 - `/quill/robotsWhoAre/Partygoer/aged/400` - All Partygoer Robots aged 300 or more (no Killer robots allowed)
 - `/quill/robotsWhoAre/Partygoer/aged/400?killer` - All Partygoer Robots aged 300 or more (even Killer robots allowed)

Url                                           | Description
--------------------------------------------- | -----------
/simple/doobie/peopleNamed/{firstName}        | People Named (only using Doobie).
/simple/quill/peopleNamed/{firstName}         | People Named a particular role (using Quill and Doobie).
/doobie/peopleWhoAre/{role}/named/{firstName} | All people with a particular role (only using Doobie).
/quill/peopleWhoAre/{role}/named/{firstName}  | All people with a particular role (using Quill and Doobie).
/doobie/trollsWhoAre/{role}/in/{state}        | All trolls with a particular role living in a particular state (only using Doobie)
/quill/trollsWhoAre/{role}/in/{state}         | All trolls with a particular role living in a particular state (using Quill and Doobie) 
/doobie/robotsWhoAre/{role}/aged/{years}?killer      | All trolls with a particular role living in a particular state who are optionally allowed to assassinate (only using Doobie)
/quill/robotsWhoAre/{role}/aged/{years}?killer       | All trolls with a particular role living in a particular state who are optionally allowed to assassinate (using Quill and Doobie)
