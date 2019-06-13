-- cassandra task full script
	
	-- creation of working keyspace
CREATE KEYSPACE killrVideo WITH replication = {
	'class': 'SimpleStrategy', 
	'replication_factor': '1'
}
AND 
durable_writes=true;

	-- switch on working keyspace
use killrVideo;

	-- creation videos table
CREATE TABLE videos(
	video_id timeuuid,
	added_date timestamp,
	description text,
	title text,
	user_id uuid,
PRIMARY KEY (video_id)
);

	-- fulfill videos table form csv
COPY videous (video_id, added_date, description, title, user_id) FROM
'C:\Users\Andrei_Kazitski\Desktop\dataData\201\noSQL\labwork\exercise-2\videos.csv'
WITH HEADER=TRUE;

	-- truncate videos table
TRUNCATE videous;
	
	-- creation videos_by_title_year table
CREATE TABLE videos_by_title_year(
	title text,
	added_year INT,
	added_date timestamp,
	description text,
	user_id uuid,
	video_id timeuuid,
PRIMARY KEY ((title, added_year), video_id)
);

	-- fulfill videos_by_title_year table form csv
COPY videos_by_title_year (title, added_year, added_date, description, user_id, video_id) FROM
'C:\Users\Andrei_Kazitski\Desktop\dataData\201\noSQL\labwork\exercise-3\videos_by_title_year.csv'
WITH HEADER=TRUE;
	
	-- creation videos_by_tag_year table
CREATE TABLE videos_by_tag_year(
	tag text,
	added_year INT,
	video_id timeuuid,
	added_date timestamp,
	description text,
	title text,
	user_id uuid,
PRIMARY KEY (tag, added_year, video_id)
)
WITH CLUSTERING ORDER BY (added_year DESC, video_id ASC);
);

	-- fulfill videos_by_tag_year table form csv
COPY videos_by_tag_year (tag, added_year, video_id, added_date, description, title, user_id) FROM
'C:\Users\Andrei_Kazitski\Desktop\dataData\201\noSQL\labwork\exercise-4\videos_by_tag_year.csv'
WITH HEADER=TRUE;

	-- add tags ceil[SET<TEXT>]
ALTER TABLE videos ADD tags SET<TEXT>;

	-- fulfill videous table form csv with tags 
COPY videous (video_id, added_date, description, tags, title, user_id) FROM
'C:\Users\Andrei_Kazitski\Desktop\dataData\201\noSQL\labwork\exercise-5\videos.csv'
WITH HEADER=TRUE;

	-- creation UDT video_encoding
CREATE TYPE video_encoding(
	bit_rates SET<TEXT>,
	encoding TEXT,
	height INT,
	width INT
);
	
	-- add encoding ceil[video_encoding]
ALTER TABLE videos ADD encoding video_encoding;

	-- fulfill videous table form csv with encoding 
COPY videous (video_id, encoding) FROM
'C:\Users\Andrei_Kazitski\Desktop\dataData\201\noSQL\labwork\exercise-5\video_encoding.csv'
WITH HEADER=TRUE;










