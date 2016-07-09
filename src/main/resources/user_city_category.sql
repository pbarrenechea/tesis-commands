create view user_city_category AS
select u.id_user , v.city, vc.category ,count(1) as total
from venue v
inner join venue_category vc on ( v.id = vc.venue_id )
inner join tips t on ( v.id = t.venue_id )
inner join users u on ( u.id_user = t.id_user )
group by u.id_user, v.city, vc.category

order by total DESC;