<?php
header('Content-Type: application/json'); // Đảm bảo dữ liệu trả về là JSON
include './lib/database.php'; // Đảm bảo đường dẫn đúng

$db = new Database();

$sql = "SELECT MONTH(ibDate) as month, SUM(ibPrice) as total 
        FROM tbl_inbox 
        WHERE ibStatus = 'Đã duyệt'
        GROUP BY MONTH(ibDate) 
        ORDER BY month ASC;";

$result = $db->select($sql);

$data = array_fill(0, 12, 0); // Tạo mảng 12 tháng, mặc định = 0

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $month = (int) $row['month']; // Chuyển đổi tháng thành số nguyên
        $data[$month - 1] = (int) $row['total']; // Cập nhật đúng vị trí
    }
}

echo json_encode($data); // Xuất dữ liệu JSON
?>
