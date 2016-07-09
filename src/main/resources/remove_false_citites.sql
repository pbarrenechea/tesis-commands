delete from ratings
where id_venue in ( select venue_id from venue
where city = 'false');

delete from tips
where id_venue in ( select venue_id from venue
where city = 'false');

delete from venue_category
where venue_id in ( select id from venue
where city = 'false');

delete from  venue
where city = 'false';