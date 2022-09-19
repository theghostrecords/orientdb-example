# Project to demonstrate a bug in orientdb version 3.2.x

# What is the bug:
Using the vertex edit mode in orientdb studio seems to break outwards facing linkbags. When pressing "Save" they are converted to LinkList. 
Nex time a new edge is added to the same vertex through the java api (Tinkerpop 2.6) the LinkList is converted to an EmbeddedList 
and the new edge is "corrupted".

An interesting observation: We add a property and an edge within the same transaction. 
If we remove the invocation of OrientVertex#setProperty and only add the edge, the database crashes (Found null entry in ridbag). 
When we set a property within the same transaction the change is written to the db and a corrupted edge is added to the EmbeddedList.

## How to use: 
* Set a password for the demodb in docker-compose.yml
* Add the same password to the application.yml
* Run docker-compose up
  * This will build the necessary images and add the demo database
  * The services will then start
* Orientdb studio can be reached through: localhost:2480
* The Java application contains two endpoints served on http://localhost:8080
  * GET /customer?id={id}
  * PUT /customer?id={id}
    * This endpoints adds a HasStayed edge from the Customer to the Hotel
    * It does this by using the method: OrientVertex#addEdge
    * It also sets a property on the Customer vertex within the same transaction (OrientVertex#setProperty)

# How to reproduce the bug:
* Open orientdb studio on http://localhost:2480 and login
* Find a customer
  * select * from Customers where @rid=#122:1
* The type of HasStayed should be Linkbag at this point:
  * select out_HasStayed.type() from Customers where @rid=#122:1 
* Open edit mode by pressing the rid
* Press Save in the upper right corner
* Go back to the previous view and query the type again. It has now been converted to a LinkList:
  * select out_HasStayed.type() from Customers where @rid=#122:1 
* Now add an edge and a property to the customer through the java application:
  * Open a terminal session: and run:
    * curl -X PUT "http://localhost:8080/customer?id=122:1"
* Go back to orientdb studio
* Query the type again. It has now been converted to EmbeddedList:
  * select out_HasStayed.type() from Customers where @rid=#122:1 
* Open the customer in edit mode again by pressing the rid
  * Open out_HasStayed in the out edges
  * See that it now has a new "corrupted embedded edge" in a json format
  
# How to make the db crash:
* Open the java app in your favorite IDE
* Remove the invocation of setProperty in TransactionWrapper#addEdge
* Open a terminal and run: docker-compose build app
* If you do this after reproducing the bug above, you need to cleanup the db:
  * Delete the db container using: docker rm <id>
  * Delete the db image using: docker rmi <image>
  * Run: docker system prune
  * Run: docker volume prune
* Run: docker-compose up
* Follow the steps mentioned above in 'How to reproduce the bug' and see that the db crashes when writing the transaction containing addEdge
