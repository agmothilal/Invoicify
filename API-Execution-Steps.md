### STEP 1: 
### As a user, I can add a company so that I can bill them for my services
```
API (POST)
http://localhost:8080/company
REQUEST  				
{"name":"CTS","address":"Address1","city":"city1","state":"state1","zip":"91367","contactName":"Mike","contactTitle":"CEO",
"contactPhoneNumber":"800-800-800"}
```

### STEP 2: 
#### As a user, I can view a list of companies that I work with.
```
API (GET)
http://localhost:8080/company
```

### STEP 3: 
#### As a user, I can update company information
```
API (PATCH)
http://localhost:8080/company/{companyId}
REQUEST
{"name":"DTS","address":"Updated Address1"}
```

### STEP 4: 
#### As a user, I can view a list of companies that I work with.
```
API (GET)
http://localhost:8080/company
```

### STEP 5: 
#### As a user, I can create invoices
```
API (POST)
http://localhost:8080/invoice
REQUEST
{"companyName":"Test","totalCost":165.5,"author":"test","paid":false,"items":[{"description":"Description","rateHourBilled":10,"ratePrice":14.5,
"flatPrice":20.5,"state":"New"}]}
```

### STEP 6: 
#### As a user, I can view a list of invoices - PENDING

### STEP 7: 
#### As a user, I can add line items to invoices
```
API (PUT)
http://localhost:8080/invoice?invoiceId={invoiceId}
REQUEST
{"invoiceNumber":1,"companyName":"Test","totalCost":205.0,"author":"test","paid":false,"items":[
{"itemId":1,"description":"Description","rateHourBilled":10,"ratePrice":14.5,"flatPrice":60.0,"state":"Modified"},
{"description":"Second item","rateHourBilled":12,"ratePrice":14.5,"flatPrice":70.0,"state":"New"}]}
```

### STEP 8: 
#### As a user, I can view a list of invoices - PENDING

### STEP 9: 
#### As a user, I can modify invoices
```
API (PUT)
http://localhost:8080/invoice?invoiceId={invoiceId}
REQUEST
{"invoiceNumber":1,"companyName":"Test","totalCost":205.0,"author":"test","paid":false,"items":[
{"itemId":1,"description":"Description","rateHourBilled":10,"ratePrice":14.5,"flatPrice":60.0,"state":"Modified"}]}
```

### STEP 10: 
#### As a user, I can view a list of invoices - PENDING

### STEP 11: 
#### As a user, I can delete invoices so that I can focus only on current and recent work. - PENDING

### STEP 12: 
#### As a user, I can view a list of unpaid invoices by company
```
API (GET)
http://localhost:8080/invoice/unpaid/Test
http://localhost:8080/invoice/unpaid/Test?pageNo=1
```

### STEP 13: 
#### As a user I would like to find an invoice by searching with the invoice number so that I can help customers
```
API (GET)
http://localhost:8080//invoice/id/1
```

### STEP 14: 
#### As a user, I can view a list of companies that I work with.
```
API (GET)
http://localhost:8080/company
```
