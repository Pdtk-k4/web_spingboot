<?php
include_once './lib/database.php';
include_once './helpers/format.php';

$db = new Database();
$fm = new Format();

if (isset($_POST['keyword']) && !empty($_POST['keyword'])) {
    $keyword = $fm->validation($_POST['keyword']);
    $keyword = "%" . $keyword . "%";

    $query = "SELECT 
                proId,
                proName,
                proImage,
                proPrice,
                proSale
              FROM 
                tbl_products
              WHERE 
                proName LIKE ?
              LIMIT 10";

    $stmt = $db->link->prepare($query);
    if ($stmt === false) {
        die("Lỗi chuẩn bị truy vấn: " . $db->link->error);
    }
    $stmt->bind_param("s", $keyword);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $salePrice = $row['proPrice'] - ($row['proPrice'] * $row['proSale'] / 100);
            $imagePath = !empty($row['proImage']) ? 'admin/uploads/' . htmlspecialchars($row['proImage']) : 'admin/uploads/default.jpg';
            echo '<div class="search-item">';
            echo '<a href="detail.php?proId=' . $row['proId'] . '" class="search-item-link">';
            echo '<img src="' . $imagePath . '" alt="' . $row['proName'] . '" class="search-item-image">';
            echo '<div class="search-item-details">';
            echo '<span class="search-item-name">' . htmlspecialchars($row['proName']) . '</span>';
            echo '<span class="search-item-price">';
            if ($row['proSale'] > 0) {
                echo '<span class="price-sales">' . number_format($salePrice) . 'đ</span> ';
                echo '<span class="price-original">' . number_format($row['proPrice']) . 'đ</span>';
            } else {
                echo number_format($row['proPrice']) . 'đ';
            }
            echo '</span>';
            if ($row['proSale'] > 0) {
                echo '<div class="sale-badge">-' . $row['proSale'] . '%</div>';
            }
            echo '</div>';
            echo '</a>';
            echo '</div>';
        }
    } else {
        echo '<div class="no-result">Không tìm thấy sản phẩm nào.</div>';
    }
} else {
    echo '<div class="no-result">Không có từ khóa tìm kiếm.</div>';
}
?>