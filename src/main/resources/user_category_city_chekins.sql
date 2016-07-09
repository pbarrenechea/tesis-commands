create view user_category_city_chekins as select u.id_user, v.city, vc.category, count(1) as total
from users u
inner join tips t on ( t.id_user = u.id_user )
inner join venue v on (t.venue_id = v.id)
inner join venue_category vc on ( vc.venue_id = v.id )
group by u.id_user, v.city, vc.category
order by total desc;