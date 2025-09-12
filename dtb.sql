create database spDahitaBook;
use spDahitaBook;
CREATE TABLE tbl_user
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username varchar(255) not NULL,
    pass varchar(255) not NULL,  
    fullname varchar(255) not NULL,
    email varchar(255) not NULL,
	phone varchar(255) not NULL,
    address varchar(255) not NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tbl_role
(  
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    namerole varchar(255) not NULL
);

CREATE TABLE tbl_user_role
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    iduser bigint not NULL,
    idrole bigint not NULL, 
    CONSTRAINT FOREIGN KEY (iduser) REFERENCES tbl_user (id),
    CONSTRAINT FOREIGN KEY (idrole) REFERENCES tbl_role (id)
);

-- 1. Thêm role ADMIN
INSERT INTO tbl_role (namerole) VALUES ('ADMIN');

-- 2. Thêm user admin (ví dụ username = admin, mật khẩu cần mã hóa trong thực tế)
INSERT INTO tbl_user (username, pass, fullname, email, phone, address)
VALUES ('admin', '$2a$10$EXWdsctsghFZGq7aDz/6duyWwtHgYFo2SbNNZK7l/ritEwbvfPxvG', 'Administrator', 'admin@example.com', '0123456789', 'Ha Noi');

-- 3. Gán role ADMIN cho user admin
INSERT INTO tbl_user_role (iduser, idrole)
VALUES (
    (SELECT id FROM tbl_user WHERE username = 'admin'),
    (SELECT id FROM tbl_role WHERE namerole = 'ADMIN')
);


CREATE TABLE tbl_category
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	catname VARCHAR(255) NOT NULL, 
    catstatus boolean NOT NULL
);

INSERT INTO tbl_category (catname, catstatus) VALUES
('Sách Văn Học', 1),
('Sách Tâm Lý - Giới Tính', 1),
('Sách Tâm Lý - Kỹ Năng Sống', 1),
('Truyện Tranh', 1),
('Sách Thiếu Nhi', 1),
('Sách Ngoại Thương', 1),
('Sách marketing - bán hàng', 1),
('Sách Phát Triển Bản Thân', 1),
('Sách Chuyên Ngành', 1);


create table tbl_supplier
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    supname varchar(255) not null
);
INSERT INTO tbl_supplier (supname) VALUES
('1980books'),
('Alphabooks'),
('Azbooks'),
('Bạch Việt'),
('Linhlanbooks'),
('Nhã Nam'),
('NXB Phụ Nữ'),
('Skybooks'),
('AZ Việt Nam');
INSERT INTO `spdahitabook`.`tbl_supplier` (`supname`) VALUES ('Văn Lang');
INSERT INTO `spdahitabook`.`tbl_supplier` (`supname`) VALUES ('Phương Trường Books');


CREATE TABLE tbl_author
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT ,
    auname VARCHAR(255) NOT NULL 
);

INSERT INTO tbl_author (auname) VALUES
('Giải Tổng'),
('Miêu Công Tử'),
('Jamie Frater'),
('Ernest Hemingway'),
('Mã Bá Dung'),
('Thought Catalog'),
('Mặc Hương Đồng Khứu'),
('Kolya Bùi'),
('Trxx & Phàm'),
('Tương Tử Bối'),
('Zeke Faux'),
('Sean Pillot De Chenecey'),
('Ma Vĩ'),
('Mèo Mốc'),
('Keigo Shinzo'),
('Cá Mập Ú'),
('Abaraya Takeichi'),
('Uma Agri'),
('Bruce Bryans'),
('Yoon Geul'),
('Châu Mộ Tư'),
('Lưu Huệ Thừa – Dư Nhất'),
('Adam Grant'),
('Scott Allan'),
('Cảnh Kỳ Tâm');
INSERT INTO `spdahitabook`.`tbl_author` (`auname`) VALUES ('Jennie Harding');


CREATE TABLE tbl_products 
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT ,
    proname VARCHAR(255) NOT NULL , 
    proimage VARCHAR(255) NOT NULL ,
    proprice INT NOT NULL ,
    procontent TEXT NOT NULL ,
    prosale int NOT NULL , 
    pronewBook boolean NOT NULL ,
    prostatus boolean NOT NULL ,
    idcat bigint NOT NULL ,
    idsup bigint NOT NULL ,
    idau bigint NOT NULL ,
    foreign key (idcat) references tbl_category (id),
    foreign key (idsup) references tbl_supplier (id),
    foreign key (idau) references tbl_author (id)
);

INSERT INTO tbl_products (
    proname, proimage, proprice, procontent, prosale, pronewBook, prostatus, idcat, idsup, idau
) VALUES (
    '999 Lá Thư Gửi Cho Chính Mình - Phiên Bản Tô Chữ Tiếng Trung',
    'anh1.jpg',
    56000,
    'Trong nắng hè oi ả, mùa đông lạnh căm, ngày thu lộng gió hay trời xuân ấm áp, chỉ cần một cuốn sách, một tách trà, một cây bút và chính bạn là có thể tạo nên khung cảnh tuyệt đẹp bên hiên nhà.\n\nKhi cuộc sống ngoài kia đã quá vội vàng, chúng ta càng cần dành ra những khoảng lặng nhỏ bé để tìm về chính mình, yêu lấy bản thân, đơn giản như ngồi đọc một cuốn sách hay ghi chép lại những điều nhỏ nhặt mà ấm áp mỗi ngày. Và cuốn sách “999 lá thư gửi cho chính mình - Phiên bản tô chữ tiếng Trung” chính thức được ra mắt mang theo sứ mệnh gửi gắm những điều bình dị, giản đơn đến những độc giả mến thương vẫn luôn nỗ lực không ngừng nghỉ ở thế giới ngoài kia. Chúng mình tin rằng những trích dẫn tiếng Việt hay nhất đính kèm với phần tô chữ tiếng Trung có trong cuốn sách này sẽ không làm bạn đọc phải thất vọng.\n\nBạn thân mến, những trang sách phủ mực in dang dở đang đợi bạn đến lấp đầy bằng những nét chữ yêu thương, bạn là một phần không thể thiếu của cuốn sách này!',
    10,0,1,1,9,2
),
 (
    'Liệt Đồ – Chỉ Em Là Ngoại Lệ',
    'anh2.jpg',
    245000,
    '',
    10,0,1,1,5,1
),
 (
    'Thái Bạch Kim Tinh Đang Phiền Muộn',
    'anh3.jpg',
    179000,
    'Mượn chuyện Tây Du kể chuyện thời nay. Huyền thoại “Tây Du Ký” dưới góc nhìn hiện đại và hài hước! Gần đây, Thái Bạch Kim Tinh Lý Trường Canh có điều phiền muộn. Chẳng là hai ngày trước, điện Linh Tiêu nhận được công văn, nói rằng nay có một vị đại đức ở Đông Thổ tới Tây Thiên bái Phật cầu kinh, nhờ Thiên Đình đỡ đần trông nom giúp, còn đính kèm cả pháp chỉ của Phật Tổ đằng sau. Việc lấy kinh sẽ do Quan Âm phụ trách phối hợp với điện Khải Minh của Lý Trường Canh. Đạo trời xưa nay vẫn vậy, hễ ai muốn leo lên cảnh giới cao hơn, đều phải vượt qua mấy kiếp nạn để thử thách. Bởi vậy điện Khải Minh mới có một chức vụ chuyên sắp xếp kiếp nạn trong tầm kiểm soát cho thần tiên hoặc phàm nhân có lai lịch, để bảo đảm kẻ đó bình an vượt qua kiếp nạn, tránh xuất hiện tình huống mất mạng hỏng cả đường tu. Có điều, Lý Trường Canh vốn tưởng chỉ cần giúp một lần, không ngờ rằng Phật Tổ đã hạn định Huyền Trang phải trải qua chín chín tám mươi mốt kiếp nạn. Đồng nghĩa với việc, ông phải tự biên ra ngần ấy cái kiếp nạn rồi tự tìm cách hóa giải nó! Lòng Lý Trường Canh thoắt chốc nặng trĩu.',
    10,0,1,1,9,5
),
 (
    'Thiên Quan Tứ Phúc - Bản Hoạt Hình - Tập 5',
    'anh4.jpg',
    179000,
    'Rơi xuống hố tội nhân, Tạ Liên cùng Tam Lang chạm trán với tướng quân Khắc Ma, biết được phần nào trận chiến 200 năm trước, khi Quốc sư Bán Nguyệt thông đồng cùng Tướng quân phe địch mở cổng, tàn sát cả thành. Ôm hận chết nên dù biến thành quỷ, Khắc Ma vẫn tìm đánh Bán Nguyệt, treo xác ả bên cạnh hố tội nhân để bồi tội với binh sĩ. Nhân lúc có đông đủ tất cả, Tạ Liên muốn đối chứng cùng quốc sư để làm rõ ẩn khuất trong câu chuyện. Tại sao Quốc sư người người tin phục lại bắt tay với phe địch? Rốt cục trong trận chiến bi thương này, ai là người đúng, ai là kẻ sai?',
    10,0,1,1,9,7
),
 (
    'Tội Ác Có Thật',
    'anh5.jpg',
    129000,
    'Tuyển tập các sự thật hấp dẫn và giật gân về những kẻ sát nhân hàng loạt, thủ lĩnh giáo phái và bậc thầy lừa đảo khét tiếng. Bao gồm những thủ pháp từ hình kỳ quặc, những vụ ám sát lạnh gáy, những tên tội phạm lừng danh, những vụ vượt ngục liều lĩnh, những tội ác chấn động lịch sử, những vũ khí giết người lạ thường... Bạn đã bao giờ nghe nói đến tra tấn bằng tiếng ồn trắng chưa? Lăng trì có phải là hình thức xử tử cổ đại tàn ác nhất? Cuộc đời Billy the Kid có thật sự giống như hình tượng huyền thoại Viễn Tây về hắn hay không? “Tảng băng trôi” của chủ đề “tội phạm có thật” đầy giật gân và không kém phần lý thú đang chờ bạn khám phá trong cuốn sách này: Những kẻ sát nhân hàng loạt ít được biết đến, Những vụ vượt ngục táo bạo, Những vũ khí giết người kỳ lạ, Những hình tượng sát nhân trên màn ảnh rộng, Và nhiều hơn thế nữa! Tuyển tập hoàn hảo này chứa đựng những thông tin hy hữu và lạ lùng đến nỗi cả những người đam mê nghiên cứu tội ác có thật cũng phải sửng sốt!',
    10,0,1,1,9,3
),
(
    'Ông Già Và Biển Cả',
    'anh6.jpg',
    89000,
    'Santiago là một ông lão đánh cá, tính đến nay ông đã tám mươi tư ngày liên tiếp trở về tay không dù ngày nào ông cũng dong thuyền ra khơi. Đến ngày thứ tám mươi lăm, ngày mà định mệnh sắp đặt cho con người vượt qua giới hạn của mình, ông lão đã có một hành trình phi thường xa ngoài đại dương. Những tưởng mình đã bắt được một con cá lớn, nhưng cuối cùng những gì ông nhận được còn vĩ đại hơn thế. Ông già và biển cả là một kiệt tác bất hủ về ý chí quật cường của con người. Lẽ sống và tinh thần của con người được Ernest Hemingway miêu tả một cách chân thực và gần gũi xuyên suốt tác phẩm. Vượt ra khỏi một ông lão làng chài vật lộn giữa trùng khơi, cuốn sách này là hiện thực của vô vàn người trong số chúng ta, là những dấu hỏi ta gieo vào cuộc sống, và cũng chính là ý nghĩa sống mà chúng ta theo đuổi trong suốt cuộc đời mình.',
    10,0,1,1,9,4
),
(
    'Tôi Muốn Ở Bên Một Người Dịu Dàng',
    'anh7.jpg',
    85000,
    'Phải chăng trong sâu thẳm, ai cũng mong được nắm tay một người dịu dàng để cùng nhau đi qua những chông chênh và vội vã của cuộc đời?. Tiếp nối sự thành công của hai cuốn sách “Điều đẹp nhất có khi là buông tay” và “Ai cũng muốn chúng mình chia tay”, “Tôi muốn ở bên một người dịu dàng” sẽ giúp ta nhận ra rằng: tình yêu không nên là gánh nặng, là áp lực mà là một hành trình - nơi hai người cùng trưởng thành, cùng chữa lành, cùng đi về phía dịu dàng. Nếu bạn mong muốn ở cạnh một người dịu dàng, trước hết, chính bạn cũng cần học cách dịu dàng. Bạn không cần gò ép bản thân thành hình mẫu của ai đó, không cần chạy theo một tình yêu không dành cho mình. Điều duy nhất bạn cần làm là trở thành một phiên bản hoàn thiện hơn của chính mình - lành mạnh, bình yên và đủ yêu thương bản thân, rồi người tử tế và dịu dàng sẽ tự tìm đến. “Rồi một ngày sẽ có người chú ý tới ánh mắt lấp lánh hay vết tàn nhang trên má và rơi vào tình yêu cùng bạn. Họ sẽ muốn bảo vệ và yêu thương bạn, sẽ ôm thật chặt và thì thầm rằng: Bạn là cả thế giới. Bởi vì bạn thực sự là cả thế giới.” Người phù hợp sẽ nhìn thấy tất cả những điều đó, và họ sẽ không muốn thay đổi bạn, không đòi hỏi điều gì ở bạn mà chỉ muốn ở bên và yêu bạn trọn vẹn như bạn vốn là. “Tôi muốn ở bên một người dịu dàng” sẽ là người bạn đồng hành với giọng nói êm đềm thay bạn nói lên những điều bạn chưa kịp nói với chính mình – rằng bạn xứng đáng với một tình yêu tử tế, bắt đầu từ việc bạn cũng đối xử dịu dàng với chính trái tim mình.',
    10,0,1,1,8,6
),
(
    'Chậm Rãi Động Lòng - Tập 2',
    'anh8.jpg',
    129000,
    'Khoảnh khắc Viêm Trì giành được chức vô địch trong giải đua xe quy mô lớn, giữ vững lời hứa mang danh hiệu quán quân về cho Nghê Thường, tình yêu của hai người như được bước sang một cánh cửa mới, tình đầu ý hợp. Vậy nhưng, mối tình của họ cũng vấp phải những chông gai trắc trở. Đó là khi người mà Nghê Thường căm ghét vì những tổn thương trong quá khứ xuất hiện trước mặt cô sau bao năm biệt tích; là khi Viêm Trì phải một lần nữa đứng trước sự lựa chọn giữa gia đình và ước mơ… Sau tất cả, tình yêu giữa Viêm Trì và Nghê Thường vẫn luôn bền chặt, đồng cam cộng khổ, nhất tâm nhất ý. Sự dịu dàng và bền bỉ của Viêm Trì đã giúp cô dần bước ra khỏi bóng tối quá khứ. Sự mạnh mẽ và thấu hiểu của Nghê Thường như nguồn động lực vô bờ giúp anh vững tay lái trên mọi vòng đua. Thuở ban sơ chậm rãi động lòng, tương lai sau này cùng nhau chậm rãi hạnh phúc. Ánh mắt ông vẫn nồng nàn như ngày nào: “Kiếp sau, em vẫn là vợ anh, được chứ?” Nghê Thường nhìn thật sâu vào mắt Viêm Trì hai giây rồi mỉm cười. Kiếp trước, anh nói vậy rồi. Cho dù là kiếp nào, em đều đồng ý. Ở bên anh. Cùng ngắm pháo hoa nhân gian, tận hưởng niềm hạnh phúc trên đời.',
    10,0,1,1,9,25
),
(
    'Hy Vọng Bạn Sẽ Ổn',
    'anh9.jpg',
    126000,
  'GIỚI THIỆU SÁCH
HY VỌNG BẠN SẼ ỔN

Khi chụp mặt trăng hay hoàng hôn, cậu nhận ra rằng ảnh chụp được không thể đẹp bằng thực tế, nhưng cậu sẽ không cho rằng trời không đẹp, vì cậu biết máy ảnh không thể bắt được vẻ đẹp của bầu trời.

Cậu nên dùng cách tương tự để nhìn bản thân, thực tế cậu cũng rực rỡ tựa ánh ráng chiều.

Trong cuộc đời này, có những thứ không thể thay thế. Như ánh nắng ban mai làm bừng sáng bóng tối đêm qua, cơn gió mùa thổi bay đi những cảm xúc ngột ngạt, tiếng sóng vỗ làm dịu đi những suy nghĩ nặng nề, những kỷ niệm một thời ta trân trọng, và cậu, người đang đọc những dòng này. Đúng, không ai có thể thay thế vị trí của cậu. 

“Hy vọng bạn sẽ ổn” - Tên cuốn sách cũng như là lời nhắn nhủ mà tác giả Yoon Geul và Vibooks muốn gửi tới cậu rằng: Hy vọng cậu cảm thấy tự hào về cuộc sống của mình. Mong rằng, cậu sẽ trở thành một người có thể nói và hành động đầy tự tin, đầy kiên trì và quyết tâm dù ở đâu, gặp ai và trong hoàn cảnh nào. Ngoại cảnh có thể không bao giờ ổn và mọi thứ không bao giờ giữ nguyên như cũ. Ở đó, tớ hy vọng cậu đã xoa dịu được mọi nỗi sợ hãi của mình, để cậu không cần phải tin vào những điều vô nghĩa như vậy.

“Nếu cậu dành thời gian, chăm sóc từng chút một cho những mầm hạnh phúc nhỏ nhoi, bông hoa xinh đẹp tựa ánh nắng ngày xuân sẽ dần nở rộ trong tim quý giá của cậu.”',
   10,0,1,2,9,20
);
INSERT INTO `spdahitabook`.`tbl_products` (`proname`, `proimage`, `proprice`, `procontent`, `prosale`, `pronewBook`, `prostatus`, `idcat`, `idsup`, `idau`) VALUES ('Thao Túng Cảm Xúc: Áp Đặt Và Định Kiến', 'anh10.jpg', '149000', ' Sách: \"  Thao Túng Cảm Xúc: Áp Đặt Và Định Kiến \"', '10', '0', '1', '2', '9', '21');



-- (
--     '',
--     '',
--     129000,
--     '',
--     10,0,1,1,12,9
-- );


CREATE TABLE tbl_productsimages 
(
	id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
    piimage VARCHAR(255) NOT NULL,
    idpro bigint NOT NULL,
    foreign key (idpro) references tbl_products (id)
);
INSERT INTO `spdahitabook`.`tbl_productsimages` (`piimage`, `idpro`) VALUES ('pi1.jpg', '9');
INSERT INTO `spdahitabook`.`tbl_productsimages` (`piimage`, `idpro`) VALUES ('pi2.jpg', '9');
INSERT INTO `spdahitabook`.`tbl_productsimages` (`piimage`, `idpro`) VALUES ('pi3.jpg', '9');
INSERT INTO `spdahitabook`.`tbl_productsimages` (`piimage`, `idpro`) VALUES ('pi4.jpg', '9');

CREATE TABLE tbl_slides
(
	id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT ,
    sltitle VARCHAR(255) NOT NULL ,
    sllink VARCHAR(355) NOT NULL ,
    slimage VARCHAR(255) NOT NULL ,
    slactive boolean NOT NULL ,
    sltarget VARCHAR(255) NOT NULL 
);
INSERT INTO tbl_slides (sltitle, sllink, slimage, slactive, sltarget) 
VALUES 
  ('slide1', '', 'slide1.png', 1, '_blank'),
  ('slide2', '', 'slide2.png', 1, '_self'),
  ('slide3', '', 'slide3.png', 1, '_blank'),
  ('slide4', '', 'slide4.png', 1, '_blank'),
  ('slide5', '', 'slide5.png', 1, '_blank');



CREATE TABLE tbl_transactions 
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT ,
    iduser bigint NOT NULL ,
    tsttotalmoney INT NOT NULL ,
    tstnote VARCHAR(355) NULL ,
    foreign key (iduser) references tbl_user (id)
);

CREATE TABLE tbl_orders 
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT ,
    idtst bigint NOT NULL ,
    idpro bigint NOT NULL ,
    odquantity INT NOT NULL,
    odprice INT NOT NULL ,
    foreign key (idtst) references tbl_transactions (id),
    foreign key (idpro) references tbl_products (id)
);

CREATE TABLE tbl_inbox 
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT ,
    idtst bigint NULL ,
    iduser bigint NOT NULL ,
    ibdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    ibquantity INT NOT NULL ,
    ibprice INT NOT NULL ,
    cusname VARCHAR(255) NOT NULL ,
    cusaddress VARCHAR(355) NOT NULL ,
    cusphone varchar(255) NOT NULL ,
    ibstatus boolean NOT NULL ,
    foreign key (iduser) references tbl_user (id)
);

CREATE TABLE tbldetailinbox
(
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT ,
    idpro bigint NOT NULL ,
    iduser bigint NOT NULL ,
    idib bigint NOT NULL,
    odquantity INT NOT NULL,
    foreign key (idpro) references tbl_products (id),
    foreign key (iduser) references tbl_user (id																																													)
);