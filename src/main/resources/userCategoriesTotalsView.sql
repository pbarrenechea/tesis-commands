CREATE View user_category_totals as
  (select u.id_user, c.name, c.id, count(*) as total
from category c
inner JOIN venue_category v on (c.id = v.category)
INNER JOIN tips t on ( t.venue_id = v.id )
INNER JOIN users u on ( u.id_user = t.id_user )
GROUP BY  c.id, c.name, u.id_user
order by u.id_user, total Desc)
;