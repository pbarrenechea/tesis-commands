create view user_checkins as select u.id_user as user, sum(total)
from user_category_totals u
GROUP BY  u.id_user;