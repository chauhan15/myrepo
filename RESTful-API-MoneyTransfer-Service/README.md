# Implement a RESTful API (including data model and the backing implementation)


#Description:
This Rest-Like service has been developed using javax.ws.rs library. It has been tried to keep this application as simple as possible. No heavy frameworks have been used, no spring framework. If required, this rest service can be scaled and enhanced. As of now, it would be covering basis functionalities like:
1. Check account balance
2. Deposit money into any account
3. Withdraw money from any account
4. Transfer money from one account to another.

#Also, ACID properties have been taken care here by making use of locks for multi threaded environment. Hence, this application/service is suitable to be used in multi threaded environment.

# Proper test cases are there, covering almost every method and functionality.

# Assumption:
It has been assumed that to implement the functionality of money transfer from one account to another, other basic functionalities should also be covered like checking the balance before initiating the transaction, deposit money into an account if it is not having sufficient funds to transfer, withdrawing some amount from the account, once transfer is success. If required, this service is purely scalable and maintainable.

# To execute the application , run the class "MoneyTransferApplication.java", it will start the server or execute following command:
# java -jar target/RESTful-API-MoneyTransfer-Service-1.0.0-SNAPSHOT.jar

By doing this, it will start the embedded Jetty server which is listening on port:8080, use following URLs to check the simulation functionalities:

#Check balance
http://localhost:8080/moneyTransferService/1/checkBalance

#Deposit Money
http://localhost:8080/moneyTransferService/1/depositMoney/10

#WithDraw Money
http://localhost:8080/moneyTransferService/1/withdrawMoney/10

#Transfer Money from One account to another
http://localhost:8080/moneyTransferService/transferMoney/1/10/2

