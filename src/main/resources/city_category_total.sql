create view city_category_total as select v.city, vc.category ,count(1) as total
from venue v
inner join venue_category vc on ( v.id = vc.venue_id )
inner join tips t on ( v.id = t.venue_id )
group by v.city, vc.category
order by total DESC;