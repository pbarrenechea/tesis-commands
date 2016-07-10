create view city_category_total as select q.city, q.category, count(1) as total from(
select v.city, vc.category, t.id_user
from venue v
inner join venue_category vc on ( v.id = vc.venue_id )
inner join tips t on ( v.id = t.venue_id )
group by v.city, vc.category, t.id_user) q
group by q.city, q.category
order by total DESC ;
