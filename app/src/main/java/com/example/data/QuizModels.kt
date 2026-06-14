package com.example.data

import com.example.R

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class QuizCategory(
    val id: String,
    val title: String,
    val description: String,
    val level: String, // "Amateur", "Medium", "Expert"
    val imageResId: Int,
    val questions: List<Question>,
    val rewardBadgeId: String,
    val rewardBadgeTitle: String,
    val rewardBadgeDesc: String,
    val rewardBadgeIcon: String // Name of graphic
)

object QuizRepository {

    private val earlyTitles = listOf(
        "Jackson 5 Motown Debut", "ABC & Spelling Hooks", "The Love You Save Mastery", "I'll Be There Ballad",
        "Steeltown Records Rarities", "Going Back to Indiana", "Ben Rodents & Friendship", "Got to Be There Debut",
        "Music & Me Vocals", "Forever, Michael Gems", "Dancing Machine Grooves", "Goin' Places Jazz Fusion",
        "Destiny Self-Produced Breakthrough", "Triumph Tour Power", "Philadelphia Intl Sessions", "Off The Wall Quincy Magic",
        "Don't Stop 'Til You Get Enough", "Rock With You Timeless Groove", "She's Out of My Life High C", "Get on the Floor Funky Bass",
        "Working Day and Night Tap Shoes", "Off the Wall Horn Arrangements", "The Wiz Scarecrow Performance", "Motown 25 Moonwalk Genesis"
    )

    private val earlyDescriptions = listOf(
        "Explore Jackson 5's historic 1969 Motown debut under Diana Ross and Berry Gordy.",
        "Uncover the spelling vocals and soul hooks of the classic student anthem ABC.",
        "Test your memory on the fiery rhythms and vocal tradeoffs of The Love You Save.",
        "Dive into the deep emotional delivery of Michael's timeless ballad of commitment.",
        "Unlock secrets of Jackson family's earliest recording contract in Gary, Indiana.",
        "Recall the nostalgic state pride and early Midwestern tours of the young band.",
        "Learn about Michael's first solo #1 hit, dedicated to a rodent companion.",
        "Perfect your knowledge on MJ's first official solo debut studio album in 1972.",
        "Master the technical vocal layers of Michael's third acoustic folk-soul album.",
        "Review the hidden, soulful ballads and Motown experiments of his early adulthood.",
        "Re-evaluate the robotics rhythms and proto-breakdancing grooves of Dancing Machine.",
        "Examine the progressive disco crossover ideas on the 1977 Philly collaboration.",
        "Discover the self-written soul and disco anthems that pioneered artistic freedom.",
        "Test your live stage trivia from Michael's spectacular 1980 arena showcase.",
        "Investigate the legendary songwriting chemistry with Gamble & Huff in Philadelphia.",
        "Revisit the Quincy Jones and Bruce Swedien studio synergy that changed pop history.",
        "Deconstruct the ecstatic screams and soda pop bottle percussions from the intro.",
        "Unveil the synth hooks and smooth drum tracks of Rod Temperton's masterpiece.",
        "Analyze the vocal vulnerability and live tearful takes behind this heartbreaking song.",
        "Test your knowledge of Louis Johnson's thumb-slapping bass genius in the groove.",
        "Deconstruct Michael's frantic rhythm writing and custom tap footwear sounds.",
        "Decode the bright jazz brass arrangements performed by Jerry Hey's elite horns.",
        "Relive Michael's physical acting performance playing the Scarecrow alongside Diana Ross.",
        "Deconstruct the historic night that shocked 47 million viewers of the Motown gala."
    )

    private val eightiesTitles = listOf(
        "Thriller Master Tapes", "Vincent Price Laugh Takes", "Billie Jean 91 Mixes", "Eddie Van Halen Beat It Solo",
        "Beat It Video Gang Truce", "Thriller Werewolf Makeup Magic", "Wanna Be Startin' Somethin' Chants", "Human Nature Soft Synth",
        "P.Y.T. Backup Sisters", "The Girl is Mine McCartney Duet", "Bad Video Scorsese Direction", "Smooth Criminal Anti-Gravity Heels",
        "The Lean 45-degree Geometry", "The Way You Make Me Feel Blue Shirt", "Man in the Mirror Ballard Anthem", "Dirty Diana Steve Stevens Solo",
        "Another Part of Me Captain EO", "Leave Me Alone Claymation Critiques", "Liberian Girl Hollywood Cameos", "Speed Demon Claymation Rabbit",
        "I Just Can't Stop Loving You Duet", "Bad World Tour 1988 Wembley", "Grammy Night 1984 8-Award Sweep", "Pepsi Pyrotechnics Incident"
    )

    private val eightiesDescriptions = listOf(
        "Dive deep into the multi-track layers of the best-selling studio album in human history.",
        "Uncover Vincent Price's blood-chilling spoken rap and studio recording sessions.",
        "Decipher Billie Jean's Bruce Swedien mix variations and custom isolation chamber drums.",
        "Test your knowledge of the blazing impromptu guitar solo performed as a favor.",
        "Explore the casting of real rival gang members in the legendary Los Angeles street video.",
        "Discover the Hollywood makeup artistry and zombie choreographies of Rick Baker.",
        "Decode the Swahili Ma-Ma-Sa chants inspired by African saxophonist Manu Dibango.",
        "Relive the dreamy analog synthesizers and Steve Porcaro songwriting credits.",
        "Recall the backing session chatter and backing vocals from Janet and La Toya.",
        "Test your history of MJ and Paul McCartney's studio duets and playful banters.",
        "Decode Martin Scorsese's 18-minute subway action music film co-starring Wesley Snipes.",
        "Unveil the patented shoe peg hardware used in Michael's trademark gravity-defying moves.",
        "Test your knowledge of the physical and mechanical limits behind the 45-degree tilt.",
        "Recall the streetlights and theatrical call-response steps with Tatiana Thumbtzen.",
        "Discover the gospel power of the Andrae Crouch Choir on this massive mirror anthem.",
        "Test your memories of Billy Idol's lead guitarist and the theatrical rock ballad.",
        "Explore George Lucas's 3D sci-fi spaceship classic Captain EO and its space anthem.",
        "Deconstruct the clayanimated video making fun of absurd supermarket tabloids.",
        "Test your knowledge of elite celebrity appearances of MJ's friends in the music video.",
        "Relive the motorcycle chases and clayanimated rabbit Spike's high-speed dance-off.",
        "Uncover the vocal chemistry with Siedah Garrett in MJ's signature love duet.",
        "Test your live show trivia from the record-breaking Wembley stadium concerts.",
        "Recall the grand 8-trophy clean sweep that shook the music industry in 1984.",
        "Investigate the historical accident during the commercial shoot that changed Michael's life."
    )

    private val legacyTitles = listOf(
        "Dangerous Cover Mark Ryden Mask", "Black or White Morphing Technology", "Remember the Time Egyptian Splendor", "In the Closet Naomi Duet",
        "Heal the World Peace Anthem", "Jam Heavy D Rap Beat", "Who Is It Deep Melatone", "Will You Be There Free Willy",
        "Give In To Me Slash Collaboration", "HIStory Statue Marketing Hype", "Scream Janet Sister Duet", "They Don't Care About Us Brazil",
        "Earth Song Environmental Passion", "You Are Not Alone R. Kelly Ballad", "Stranger in Moscow Rainfall Raincoat", "Blood on the Dance Floor Red Tux",
        "Ghost Video 39-Minute Records", "Invincible Hardest-to-produce Album", "Butterflies Floetry Neo Soul", "You Rock My World Brando Accent",
        "One More Chance Last Single", "This Is It O2 Arena Plans", "Michael Posthumous Album", "Xscape Timbaland Modernization"
    )

    private val legacyDescriptions = listOf(
        "Deconstruct the dense symbolic paintings of Mark Ryden on the 1991 Dangerous sleeve.",
        "Test your knowledge of John Landis's video featuring early digital morphing tech.",
        "Relive the Pharaoh settings, Eddie Murphy, and Magic Johnson cameos in Egypt.",
        "Deconstruct the desert setting, raw falsetto vocals, and Naomi Campbell's steps.",
        "Discover the global humanitarian foundations and choirs of Michael's peace anthem.",
        "Revisit the heavy basketball shootouts with Michael Jordan and Heavy D's swift rap.",
        "Explore the dark, melancholic mystery of this classical-groove hit of heartbreak.",
        "Test your recall on the angelic introductory chorale and the cinematic theme song.",
        "Revisit Slash's fiery guitar work on this physical alternative hard rock hit.",
        "Investigate the massive floating steel statues erected across Europe in 1995.",
        "Relive the record-breaking 7-million-dollar space station sibling collaboration.",
        "Review Spike Lee's raw shoot in Rio de Janeiro's favelas and Olodum drummers.",
        "Test your knowledge of the climate warning anthem that topped the European charts.",
        "Perfect your memory on the slow-tempo ballad that set a Billboard Hot 100 debut record.",
        "Deconstruct the lonely, rainy walks in Moscow and MJ's personal self-reflection.",
        "Recall the red leather tuxedos and salsa steps with Sybil Buck on the dancefloor.",
        "Discover Michael's Guinness record-holding 39-minute ghost mansion cinematic video.",
        "Test your trivia of the 2001 high-fidelity technical production on MJ's final LP.",
        "Delve into the dreamy falsetto and backing melodies written by Marsha Ambrosius.",
        "Decode Marlon Brando's cameo and Michael's playful hats and retro street fights.",
        "Analyze the last official single released in Michael's lifetime in 2003.",
        "Unveil the set designs, choreographers, and master plans for the 50 London concerts.",
        "Examine the posthumous 2010 collection of tracks completed by Teddy Riley.",
        "Explore Timbaland and StarGate's sonic reinventions on the 2014 archive release."
    )

    // Master list of 15 questions for the Early Era
    private val earlyMasterQuestions = listOf(
        Question(
            id = 11,
            text = "In which year did The Jackson 5 release their breakthrough Motown debut single 'I Want You Back'?",
            options = listOf("1968", "1969", "1970", "1971"),
            correctOptionIndex = 1,
            explanation = "Released in October 1969, 'I Want You Back' became the band's first national hit and went on to sell millions of copies worldwide."
        ),
        Question(
            id = 12,
            text = "Before signing with Motown in 1968, what local independent record label did The Jackson 5 record for in Indiana?",
            options = listOf("Sun Records", "Steeltown Records", "Chess Records", "Vee-Jay Records"),
            correctOptionIndex = 1,
            explanation = "In Gary, Indiana, the family group first signed with Steeltown Records in late 1967 and recorded their absolute earliest tracks."
        ),
        Question(
            id = 13,
            text = "In which Midwestern city was Michael Jackson born, and raised alongside his brothers and sisters?",
            options = listOf("Detroit, MI", "Gary, IN", "Chicago, IL", "Cleveland, OH"),
            correctOptionIndex = 1,
            explanation = "Michael Jackson was born on August 29, 1958, in the steel-mill city of Gary, Indiana, moving the family to California after J5 signed with Motown."
        ),
        Question(
            id = 14,
            text = "The Jackson 5 made history when their first four consecutive Motown singles hit #1 on the US Billboard charts. Which of these is NOT one of those four?",
            options = listOf("ABC", "The Love You Save", "I'll Be There", "Dancing Machine"),
            correctOptionIndex = 3,
            explanation = "The first four #1s were 'I Want You Back', 'ABC', 'The Love You Save', and 'I'll Be There'. 'Dancing Machine' came later in 1973, reaching #2."
        ),
        Question(
            id = 15,
            text = "Michael's first solo #1 hit single was released in 1972 under Motown Records. What friendly creature was this track named after?",
            options = listOf("A parrot named Rockin' Robin", "A pet rat named Ben", "A chimpanzee named Bubbles", "A black dog named Charlie"),
            correctOptionIndex = 1,
            explanation = "'Ben', a beautiful ballad about a pet rat from the 1972 movie theme, became Michael's first solo #1 hit on the Billboard Hot 100 at age 14."
        ),
        Question(
            id = 16,
            text = "Which legendary singer-songwriter took credit for 'discovering' The Jackson 5 and served as a crucial early mentor?",
            options = listOf("Aretha Franklin", "Diana Ross", "Gladys Knight", "Etta James"),
            correctOptionIndex = 1,
            explanation = "Motown historically promoted Diana Ross as J5's primary discoverer. She hosted them at her home in Los Angeles and introduced them to the public."
        ),
        Question(
            id = 17,
            text = "Which 1978 musical film saw Michael co-star alongside Diana Ross, playing the physical role of the Scarecrow?",
            options = listOf("The Wiz", "West Side Story", "Hair", "Cabaret"),
            correctOptionIndex = 0,
            explanation = "Michael played the Scarecrow in Sidney Lumet's 'The Wiz', an African-American adaptation of The Wizard of Oz. It was here he met Quincy Jones."
        ),
        Question(
            id = 18,
            text = "Which legendary master record producer did Michael meet on the set of 'The Wiz' and hire to construct his solo albums?",
            options = listOf("Berry Gordy", "Clive Davis", "Quincy Jones", "Rick Rubin"),
            correctOptionIndex = 2,
            explanation = "Quincy Jones was the musical director of 'The Wiz'. After Michael asked him for references, Quincy offered to produce his next solo album himself."
        ),
        Question(
            id = 19,
            text = "Who wrote the smooth-tempo disco ballad single 'Rock with You' from the historic 1979 album 'Off the Wall'?",
            options = listOf("Michael Jackson", "Rod Temperton", "Quincy Jones", "Paul McCartney"),
            correctOptionIndex = 1,
            explanation = "Rod Temperton, former keyboardist of the soul-funk band Heatwave, wrote both 'Rock with You' and the later classic title song 'Thriller'."
        ),
        Question(
            id = 20,
            text = "What was the official energetic lead single off the landmark 1979 solo album 'Off the Wall'?",
            options = listOf("Rock with You", "Don't Stop 'Til You Get Enough", "Off the Wall", "She's Out of My Life"),
            correctOptionIndex = 1,
            explanation = "'Don't Stop 'Til You Get Enough' was the first single, showcasing Michael's signature high falsetto and raw percussion using soda bottles."
        ),
        Question(
            id = 21,
            text = "Which heart-wrenching ballad from 'Off the Wall' famously ends with Michael sobbing in emotional distress during the final takes?",
            options = listOf("Girlfriend", "She's Out of My Life", "I Can't Help It", "It's the Falling in Love"),
            correctOptionIndex = 1,
            explanation = "Michael Jackson was so deeply connected to the lyrics of 'She's Out of My Life' that he burst into real tears at the end of every recording take."
        ),
        Question(
            id = 22,
            text = "Who was the executive director and founder of Motown Records who signed J5 to their first major national recording contract?",
            options = listOf("Sam Cooke", "Berry Gordy", "Al Bell", "Ahmet Ertegun"),
            correctOptionIndex = 1,
            explanation = "Berry Gordy founded Motown in Detroit. He signed J5 in 1968, housing them in California and supervising their legendary arrangements."
        ),
        Question(
            id = 23,
            text = "In 1976, J5 moved from Motown to Epic Records and changed their name. What was their new name due to trademark restrictions?",
            options = listOf("The Jacksons", "The Gary Brothers", "Jackson Pride", "Michael & Brothers"),
            correctOptionIndex = 0,
            explanation = "Motown owned the name 'The Jackson 5'. When the group moved to Epic, they had to legally change their name to 'The Jacksons' without Jermaine."
        ),
        Question(
            id = 24,
            text = "Which self-produced 1978 Jacksons album saw Michael and his brothers establish full creative control over their musical output?",
            options = listOf("Destiny", "Triumph", "Victory", "Goin' Places"),
            correctOptionIndex = 0,
            explanation = "Released in late 1978, 'Destiny' was written and produced entirely by the brothers themselves, containing the massive dance anthem 'Shake Your Body'."
        ),
        Question(
            id = 25,
            text = "What was the name of the oldest Jackson brother who initially formed the 'Jackson Brothers' trio before Michael joined?",
            options = listOf("Tito", "Jackie", "Jermaine", "Marlon"),
            correctOptionIndex = 1,
            explanation = "Jackie Jackson was the oldest brother who, alongside Tito and Jermaine, formed the original group in 1964. Michael joined later."
        )
    )

    // Master list of 15 questions for the 80s Pop Icon Era
    private val eightiesMasterQuestions = listOf(
        Question(
            id = 31,
            text = "Exactly how many non-consecutive weeks did the historic 1982 album 'Thriller' spend at #1 on the US Billboard 200?",
            options = listOf("28 weeks", "32 weeks", "37 weeks", "40 weeks"),
            correctOptionIndex = 2,
            explanation = "'Thriller' spent an outstanding, record-breaking 37 weeks at #1, establishing a record for solo recording artists that remains unbroken."
        ),
        Question(
            id = 32,
            text = "Who performed the iconic horror voice-over narration and dramatic evil laugh at the end of the song 'Thriller'?",
            options = listOf("Christopher Lee", "Vincent Price", "Boris Karloff", "Bela Lugosi"),
            correctOptionIndex = 1,
            explanation = "The grand master of horror Vincent Price recorded the memorable final voice-over and theatrical laughter in only two takes."
        ),
        Question(
            id = 33,
            text = "Which rock guitar legend performed the blistering, impromptu guitar solo in Michael's hit song 'Beat It'?",
            options = listOf("Eddie Van Halen", "Slash", "Steve Lukather", "Eric Clapton"),
            correctOptionIndex = 0,
            explanation = "Eddie Van Halen played the legendary solo for free as a personal favor. His heavy amplifier caught on fire during the studio sessions!"
        ),
        Question(
            id = 34,
            text = "During which historic 1983 televised special did Michael Jackson introduce his legendary Moonwalk dance to the world?",
            options = listOf("The 25th Annual Grammy Awards", "Motown 25: Yesterday, Today, Forever", "MTV Video Music Awards", "American Music Awards"),
            correctOptionIndex = 1,
            explanation = "Michael performed 'Billie Jean' on March 25, 1983 for Motown 25, delivering the Moonwalk to a historic viewer audience of 47 million people."
        ),
        Question(
            id = 35,
            text = "In 'Smooth Criminal', exactly how many degrees of gravity-defying tilt do Michael and his backup dancers accomplish in unison?",
            options = listOf("30 degrees", "45 degrees", "55deg", "60 degrees"),
            correctOptionIndex = 1,
            explanation = "Using custom patented mechanical shoes, the performers lean forward at a physics-defying 45-degree angle without falling over."
        ),
        Question(
            id = 36,
            text = "What was the unique physical invention co-patented by Michael Jackson to permit the 45-degree lean during live tours?",
            options = listOf("Built-in steel toe springboards", "A system of strong magnets", "Slotted heels that lock onto stage pegs", "Retractable ankle wires"),
            correctOptionIndex = 2,
            explanation = "Michael co-patented a mechanism (US Patent 5,255,452) where stage pegs slide into custom-cut slots in the performers' heels on cue."
        ),
        Question(
            id = 37,
            text = "What was the color scheme of the iconic zippered jacket designed by Deborah Landis for the 'Thriller' short film?",
            options = listOf("Black with gold zippers", "Bright red with black V-shaped strips", "Silver metallic with red trim", "Blue satin with white stripes"),
            correctOptionIndex = 1,
            explanation = "The bright candy-red jacket was custom created with bold black lines so that Michael would stand out cleanly against the zombie crowd."
        ),
        Question(
            id = 38,
            text = "Setting a single-night world record, exactly how many Grammy trophies did Michael sweep during the 1984 Grammy Awards?",
            options = listOf("Five Awards", "Seven Awards", "Eight Awards", "Nine Awards"),
            correctOptionIndex = 2,
            explanation = "Michael won a historic eight Grammys on February 28, 1984, including Album of the Year for Thriller, the most ever won in a single night."
        ),
        Question(
            id = 39,
            text = "In the 'Smooth Criminal' music video, which 1930s-inspired vintage color theme was chosen for Michael's famous suit?",
            options = listOf("Yellow suit with red tie", "White chalk-stripe suit and blue armband", "Red leather suit with silver studs", "Solid black suit with golden sash"),
            correctOptionIndex = 1,
            explanation = "Inspired by Fred Astaire, Michael wore a customized double-breasted white suit, a blue sleeve armband, and a white fedora with blue band."
        ),
        Question(
            id = 40,
            text = "Which legendary songwriters wrote 'Man in the Mirror', the gospel-driven #1 hit from the Bad album?",
            options = listOf("Michael Jackson & Quincy Jones", "Rod Temperton", "Siedah Garrett & Glen Ballard", "Stevie Wonder"),
            correctOptionIndex = 2,
            explanation = "Siedah Garrett and Glen Ballard wrote the song, with backing vocals provided by Garrett and the monumental Andrae Crouch Choir."
        ),
        Question(
            id = 41,
            text = "Which rock guitar specialist provided the raw, energetic guitar lead in the theatrical rock ballad 'Dirty Diana'?",
            options = listOf("Eddie Van Halen", "Steve Stevens", "Slash", "Joe Perry"),
            correctOptionIndex = 1,
            explanation = "Steve Stevens, famous for his work with Billy Idol, contributed the hard-hitting heavy metal guitar solos to match Michael's raw passion."
        ),
        Question(
            id = 42,
            text = "To achieve the perfect, legendary bass-heavy mix on 'Billie Jean', sound engineer Bruce Swedien did how many separate mix takes?",
            options = listOf("4 times", "25 times", "50 times", "91 times"),
            correctOptionIndex = 3,
            explanation = "Bruce Swedien completed 91 mixes! Ultimately, Michael loved mix number 2, which was selected for the final master pressed on vinyl."
        ),
        Question(
            id = 43,
            text = "What was the skin condition Michael was diagnosed with in the mid-1980s, which led him to wear makeup and his signature medical tape?",
            options = listOf("Vitiligo", "Lupus", "Psoriasis", "Albinism"),
            correctOptionIndex = 0,
            explanation = "Michael was diagnosed with Vitiligo, an autoimmune condition causing patchy loss of pigmentation, leading him to protect and blend his skin."
        ),
        Question(
            id = 44,
            text = "What was the first music video by an African-American artist to receive heavy rotation on the newly-launched MTV channel?",
            options = listOf("Rock with You", "Billie Jean", "Beat It", "Thriller"),
            correctOptionIndex = 1,
            explanation = "The high visual quality and massive popularity of 'Billie Jean' officially broke down racial barriers at MTV, paving the way for future black artists."
        ),
        Question(
            id = 45,
            text = "Which legendary Scorsese-directed 18-minute short film was set in a Brooklyn subway station and co-starred a young Wesley Snipes?",
            options = listOf("Thriller", "Bad", "Beat It", "Speed Demon"),
            correctOptionIndex = 1,
            explanation = "Martin Scorsese directed 'Bad' as a gritty musical street film. Co-starring Wesley Snipes, it became an instant visual triumph."
        )
    )

    // Master list of 15 questions for the Legacy Era
    private val legacyMasterQuestions = listOf(
        Question(
            id = 51,
            text = "Which famous surrealist canvas artist created the incredibly dense, masking-themed painting for the cover of 'Dangerous' (1991)?",
            options = listOf("Andy Warhol", "Keith Haring", "Mark Ryden", "Salvador Dali"),
            correctOptionIndex = 2,
            explanation = "The majestic cover art was painted by the master of pop surrealism, Mark Ryden. It took him over six detail-oriented months to complete."
        ),
        Question(
            id = 52,
            text = "The 1991 video of 'Black or White' famously pioneered which digital visual effects technology during its global-unity climax?",
            options = listOf("Green screen keying", "Digital Face Morphing", "Bullet time slowdown", "3D CGI Wireframing"),
            correctOptionIndex = 1,
            explanation = "The final minutes showing diverse people seamlessly morphing into each other was a historical breakthrough for digital face morphing technology."
        ),
        Question(
            id = 53,
            text = "Set in Ancient Egypt, which music video features cameos from Magic Johnson, Eddie Murphy, and Iman?",
            options = listOf("Remember the Time", "In the Closet", "Jam", "Will You Be There"),
            correctOptionIndex = 0,
            explanation = "John Singleton directed the epic 'Remember the Time' video set in Pharaoh Ramses' palace, introducing ground-breaking Egyptian steps."
        ),
        Question(
            id = 54,
            text = "Which beautiful supermodel played the lead female dancer in the sandy, high-contrast, desert-themed video of 'In the Closet'?",
            options = listOf("Cindy Crawford", "Naomi Campbell", "Tyra Banks", "Iman"),
            correctOptionIndex = 1,
            explanation = "Naomi Campbell performed alongside Michael in the sensual, sepia-toned desert video directed by celebrity photographer Herb Ritts."
        ),
        Question(
            id = 55,
            text = "Setting a world-record for the most expensive music video ever made at $7 million, 'Scream' was a sci-fi duet with which sibling?",
            options = listOf("Jermaine Jackson", "La Toya Jackson", "Janet Jackson", "Rebbie Jackson"),
            correctOptionIndex = 2,
            explanation = "Directed by Mark Romanek, the futuristic black-and-white video featured Michael and Janet Jackson venting in a spacecraft, costing a historic $7M."
        ),
        Question(
            id = 56,
            text = "Michael's epic पर्यावरण (environmental) statement 'Earth Song' featured spectacular visuals. On tour, how did he elevate above the stage?",
            options = listOf("Via rocket jetpack packs", "Suspended on a rising crane/scaffolding bridging the fans", "In a floating metallic saucer", "By mechanical wire levitation"),
            correctOptionIndex = 1,
            explanation = "Michael performed the climax of 'Earth Song' on a scissor-jack crane that reached out over the front rows, symbolizing global warning."
        ),
        Question(
            id = 57,
            text = "In 1993, Michael stood frozen on a capsule elevator for two full minutes before initiating which massive televised showcase?",
            options = listOf("Grammy Awards 1993", "Super Bowl XXVII Halftime Show", "MTV Movie Awards 10th Anniversary", "Soul Train Heritage Awards"),
            correctOptionIndex = 1,
            explanation = "Michael's historic Super Bowl Halftime performance revolutionized modern sports television, attracting the highest ratings in history."
        ),
        Question(
            id = 58,
            text = "Which legendary screen actor provided the dramatic spoken conversation at the introduction of Michael's 2001 hit 'You Rock My World'?",
            options = listOf("Robert De Niro", "Marlon Brando", "Al Pacino", "Paul Newman"),
            correctOptionIndex = 1,
            explanation = "The legendary Marlon Brando, a close personal friend of Michael, recorded the cinematic spoken intro for the short film."
        ),
        Question(
            id = 59,
            text = "In which historic city did Michael film the raw, controversial, street-unity video of 'They Don't Care About Us' featuring Olodum drummers?",
            options = listOf("New York City, USA", "Rio de Janeiro, Brazil", "Moscow, Russia", "Johannesburg, South Africa"),
            correctOptionIndex = 1,
            explanation = "Directed by Spike Lee, 'They Don't Care About Us' was filmed in the Dona Marta favela of Rio de Janeiro, with local Olodum percussion groups."
        ),
        Question(
            id = 60,
            text = "What was the title of the massive 50-show residency in London's O2 Arena that Michael was preparing for in 2009?",
            options = listOf("The Final Curtain", "King of Pop Forever", "This Is It", "Legacy Live"),
            correctOptionIndex = 2,
            explanation = "'This Is It' was scheduled to launch in July 2009. Michael's final rehearsal recordings were transformed into a historic documentary film."
        ),
        Question(
            id = 61,
            text = "Which 1997 studio collection holds the world record as the best-selling remix album of all time, selling over 6 million copies?",
            options = listOf("Blood on the Dance Floor: HIStory in the Mix", "Michael Jackson Remixes", "Dangerous Reloaded", "Invincible Remix Volume 1"),
            correctOptionIndex = 0,
            explanation = "'Blood on the Dance Floor' remains the undisputed king of remix albums, showcasing five fresh tracks and eight historical remixes."
        ),
        Question(
            id = 62,
            text = "Michael's angelic tracking 'Will You Be There' was the cinematic main theme song of which heartwarming 1993 film?",
            options = listOf("E.T. the Extra-Terrestrial", "Free Willy", "The Lion King", "Homeward Bound"),
            correctOptionIndex = 1,
            explanation = "'Will You Be There', featuring the Cleveland Orchestra, became the signature anthem of the movie 'Free Willy', winning an MTV Movie Award."
        ),
        Question(
            id = 63,
            text = "What was the name of Michael's final completed full-length studio album, released in August 2001?",
            options = listOf("Dangerous", "HIStory: Past, Present and Future", "Invincible", "Xscape"),
            correctOptionIndex = 2,
            explanation = "Michael's final studio release was 'Invincible' in late 2001, featuring state-of-the-art production and the hit 'You Rock My World'."
        ),
        Question(
            id = 64,
            text = "Which legendary guitar specialist collaborated with Michael on 'Give In To Me' and live on his 1992 Dangerous World Tour?",
            options = listOf("Eddie Van Halen", "Slash", "Steve Stevens", "Carlos Santana"),
            correctOptionIndex = 1,
            explanation = "Slash of Guns N' Roses recorded massive guitar tracking for both 'Give In To Me' and 'Black or White', performing them live with Michael."
        ),
        Question(
            id = 65,
            text = "Released posthumously in 2014, which album saw Michael's archive vocals modernized by producer Timbaland?",
            options = listOf("Michael", "Xscape", "Immortal", "Scream 2014"),
            correctOptionIndex = 1,
            explanation = "'Xscape', curated by LA Reid and modernized by Timbaland, StarGate, and Rodney Jerkins, produced the global hit 'Love Never Felt So Good'."
        )
    )

    // Build the 72 categories dynamically (24 per Era)
    val categories: List<QuizCategory> = buildList {
        // 1. Early Era (24 categories)
        for (i in 0 until 24) {
            val title = earlyTitles[i]
            val desc = earlyDescriptions[i]
            val id = "early_$i"
            val level = if (i % 3 == 0) "Amateur" else if (i % 3 == 1) "Medium" else "Expert"
            val badgeId = "badge_early_$i"
            
            // Deterministic 5 questions selection from the 15 earlyMasterQuestions
            val questions = (0 until 5).map { j ->
                val masterQ = earlyMasterQuestions[(i + j) % 15]
                masterQ.copy(id = i * 100 + j)
            }
            
            add(
                QuizCategory(
                    id = id,
                    title = title,
                    description = desc,
                    level = level,
                    imageResId = R.drawable.img_mj_banner, // Early Era Banner
                    questions = questions,
                    rewardBadgeId = badgeId,
                    rewardBadgeTitle = "Early Era Medal #$i",
                    rewardBadgeDesc = "Unlocked by completing '$title' with 80% or higher.",
                    rewardBadgeIcon = "medal"
                )
            )
        }
        
        // 2. 80s Pop Icon (24 categories)
        for (i in 0 until 24) {
            val title = eightiesTitles[i]
            val desc = eightiesDescriptions[i]
            val id = "eighties_$i"
            val level = if (i % 3 == 0) "Amateur" else if (i % 3 == 1) "Medium" else "Expert"
            val badgeId = "badge_eighties_$i"
            
            val questions = (0 until 5).map { j ->
                val masterQ = eightiesMasterQuestions[(i + j) % 15]
                masterQ.copy(id = (i + 24) * 100 + j)
            }
            
            add(
                QuizCategory(
                    id = id,
                    title = title,
                    description = desc,
                    level = level,
                    imageResId = R.drawable.img_app_icon_fg, // Toe Stand / Fedora style
                    questions = questions,
                    rewardBadgeId = badgeId,
                    rewardBadgeTitle = "80s Pop Badge #$i",
                    rewardBadgeDesc = "Unlocked by completing '$title' with 80% or higher.",
                    rewardBadgeIcon = "fedora"
                )
            )
        }
        
        // 3. Legacy (24 categories)
        for (i in 0 until 24) {
            val title = legacyTitles[i]
            val desc = legacyDescriptions[i]
            val id = "legacy_$i"
            val level = if (i % 3 == 0) "Amateur" else if (i % 3 == 1) "Medium" else "Expert"
            val badgeId = "badge_legacy_$i"
            
            val questions = (0 until 5).map { j ->
                val masterQ = legacyMasterQuestions[(i + j) % 15]
                masterQ.copy(id = (i + 48) * 100 + j)
            }
            
            add(
                QuizCategory(
                    id = id,
                    title = title,
                    description = desc,
                    level = level,
                    imageResId = R.drawable.img_mj_vinyl, // Legacy LP Record
                    questions = questions,
                    rewardBadgeId = badgeId,
                    rewardBadgeTitle = "Legacy Crest #$i",
                    rewardBadgeDesc = "Unlocked by completing '$title' with 80% or higher.",
                    rewardBadgeIcon = "vinyl"
                )
            )
        }
    }

    fun getCategoryById(id: String): QuizCategory? {
        return categories.find { it.id == id }
    }
}
