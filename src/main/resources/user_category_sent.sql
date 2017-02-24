create view user_city_category_sent as (select sr.user_id , c.id as category, v.city,
sum(sr.sentiment)/count(1) as cat_sent
from sentiment_ratings sr
inner join venue v on ( v.id = sr.venue_id  )
inner join venue_category vc on ( vc.venue_id = v.id )
inner join category c on ( vc.category = c.id  )
where (v.city = 'New York' OR v.city = 'Los Angeles')
GROUP BY  sr.user_id, c.id, v.city);