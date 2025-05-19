<?php
include 'inc/header.php';
include_once './lib/database.php';
include_once './helpers/format.php';

$db = new Database();
$fm = new Format();

$keyword = isset($_GET['keyword']) ? $fm->validation($_GET['keyword']) : '';
$keyword_search = "%" . $keyword . "%";
?>

<div class="container lth-1">
    <h2 class="lth-2">Tìm kiếm</h2>
    <p class="lth-3">
        Có <span class="lth-4"><?php 
            $stmt = $db->link->prepare("SELECT COUNT(*) as total FROM tbl_products WHERE proName LIKE ?");
            $stmt->bind_param("s", $keyword_search);
            $stmt->execute();
            $result = $stmt->get_result();
            $row = $result->fetch_assoc();
            echo $row['total'];
        ?></span> sản phẩm cho tìm kiếm
    </p>
    <hr class="lth-5">
    <h3 class="lth-6">Kết quả tìm kiếm cho “<?php echo htmlspecialchars($keyword); ?>”.</h3>
    <div class="row lth-7">
        <?php
        if (!empty($keyword)) {
            $query = "SELECT 
                        proId,
                        proName,
                        proImage,
                        proPrice,
                        proSale
                      FROM 
                        tbl_products
                      WHERE 
                        proName LIKE ?";

            $stmt = $db->link->prepare($query);
            if ($stmt === false) {
                die("Lỗi chuẩn bị truy vấn: " . $db->link->error);
            }
            $stmt->bind_param("s", $keyword_search);
            $stmt->execute();
            $result = $stmt->get_result();

            if ($result->num_rows > 0) {
                while ($row = $result->fetch_assoc()) {
                    $salePrice = $row['proPrice'] - ($row['proPrice'] * $row['proSale'] / 100);
                    $imagePath = !empty($row['proImage']) ? 'admin/uploads/' . htmlspecialchars($row['proImage']) : 'admin/uploads/default.jpg';
                    ?>
                    <div class="col-md-3 mb-4 lth-8">
                        <div class="lth-9 product-items">
                            <?php if ($row['proSale'] > 0): ?>
                                <div class="lth-10 sale-badge">-<?php echo $row['proSale']; ?>%</div>
                            <?php endif; ?>
                            <a href="detail.php?proId=<?php echo $row['proId']; ?>">
                                <img src="<?php echo $imagePath; ?>" alt="<?php echo $row['proName']; ?>" class="product-image">
                                <h5 class="product-names"><?php echo htmlspecialchars($row['proName']); ?></h5>
                                <?php if ($row['proSale'] > 0): ?>
                                    <p class="product-prices">
                                        <span class="price-sales"><?php echo number_format($salePrice); ?>đ</span>
                                        <span class="price-original"><?php echo number_format($row['proPrice']); ?>đ</span>
                                    </p>
                                <?php else: ?>
                                    <p class="product-prices"><?php echo number_format($row['proPrice']); ?>đ</p>
                                <?php endif; ?>
                            </a>
                        </div>
                    </div>
                    <?php
                }
            } else {
                echo '<p class="lth-9 no-result">Không tìm thấy sản phẩm nào phù hợp.</p>';
            }
        } else {
            echo '<p class="lth-9 no-result">Vui lòng nhập từ khóa tìm kiếm.</p>';
        }
        ?>
    </div>
</div>

<?php include 'inc/footer.php'; ?>