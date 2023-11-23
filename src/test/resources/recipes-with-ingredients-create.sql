INSERT INTO recipes (id, complexity, created_at, description, name, image_key, products_cost, seconds_duration, author_id)
VALUES (1, 'EASY', now(), 'Recipe One', 'First recipe', null, 100, 1000, 1),
       (2, 'MEDIUM', now(), 'Recipe Two', 'Second recipe', null, 200, 2000, 2),
       (3, 'EASY', now(), 'Recipe Three', 'Third recipe',  null, 300, 3000, 3),
       (4, 'HARD', now(), 'Recipe Four', 'Fourth recipe', null,  400, 4000, 4);

INSERT INTO ingredients (id, name, created_at, image_key)
VALUES (1, 'First ingredient', now(), 1),
       (2, 'Second ingredient', now(), 2),
       (3, 'Third ingredient', now(), 3),
       (4, 'Fourth ingredient', now(), 4);

INSERT INTO recipes_ingredients (recipe_id, ingredient_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4);