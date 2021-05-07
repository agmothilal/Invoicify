company(CRU)
	company_id
	name UNIQUE
	address
	city
	state
	zip
	contact_name
	contact_title
	contact_phone_number

item(CRU)
	item_id
	description
	quantity
	total_fee
	rate
	rate_quantity
	flat_amount
	
invoice(CRUD)
	invoice_id
	company_id
	total_cost
	create_dt(older > 1 year and are marked "paid")
	author
	status(paid/unpaid)
	modified_dt

item2inoice(CRUD)
	item_id
	invoice_id
	quantity
	total_fee
	
	
paging repository