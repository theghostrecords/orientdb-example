# Project to demonstrate a bug in orientdb version 3.2.*

# What is the bug:
Using the vertex edit mode to in orientdb studio seems to break outwards facing linkbags.
When pressing "Save" they are converted to LinkList. 
Nex time a new edge is added to this vertex through the java api (Tinkerpop 2.6) the LinkList is converted to an EmbeddedList 
and the new edge is "corrupt". 

## How to use: 
* Add the root password for the demodb to the database service in docker-compose.yml
* Add the root password for the demodb to application.yml
* Run docker-compose up
  * This will build the necessary images and add the demo database
  * The services will then start
* Orientdb studio can be reached through: localhost:2480
* The Java application contains two endpoints served on http://localhost:8080
  * GET /customer?id={id}
  * PUT /customer?id={id}
    * This endpoints adds an HasStayed edge from the Customer to the Hotel
    * It does this by using the method: OrientVertex#addEdge

# How to replicate the bug:
* Open orientdb studio on http://localhost:2480 and login
* Find a customer
  * select * from Customers where @rid=#122:1
* The type of HasStayed should be Linkbag at this point:
  * select out_HasStayed.type() from Customers where @rid=#122:1 
* Open edit mode by pressing the rid
* Press Save in the upper right corner
* Go back to the previous view and query the type again. It has now been converted to a LinkList:
  * select out_HasStayed.type() from Customers where @rid=#122:1 
* Now add an edge to the customer through the java application:
  * Open a terminal session:
  * curl -X PUT "http://localhost:8080/customer?id=122:1"
* Go back to orientdb studio
* Query the type again. It has now been converted to a EmbeddedList:
  * select out_HasStayed.type() from Customers where @rid=#122:1 
* Open the customer in edit mode again by pressing the rid
  * Open out_HasStayed in the out edges
  * See that it now has a new "corrupted embedded edge" in a json format
