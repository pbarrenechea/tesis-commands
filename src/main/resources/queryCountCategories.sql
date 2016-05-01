select c.name, c.id, count(*) as totals
from category c
INNER JOIN venue_category vc on ( vc.category = c.id )
INNER JOIN tips t on ( t.venue_id = vc.venue_id)
group by c.id
order by totals desc;

select count(*) as totals
from category c
INNER JOIN venue_category vc on ( vc.category = c.id )
INNER JOIN tips t on ( t.venue_id = vc.venue_id)