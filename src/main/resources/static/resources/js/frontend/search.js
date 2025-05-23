$(document).ready(function() {
    // Cập nhật badge giỏ hàng
    function updateCartBadge() {
        $.ajax({
            url: '/client/cart/total-unique-quantity',
            type: 'GET',
            headers: {
                'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content') || ''
            },
            success: function(response) {
                $('#cartBadge').text(response.totalUniqueQuantity || 0);
            },
            error: function(xhr) {
                console.error('Lỗi cập nhật badge giỏ hàng:', xhr.status, xhr.responseText);
                $('#cartBadge').text('0');
            }
        });
    }
    updateCartBadge();
    setInterval(updateCartBadge, 10000);

    // Xử lý tìm kiếm thời gian thực
    let debounceTimer;
    $('.input-search').on('input', function() {
        clearTimeout(debounceTimer);
        var query = $(this).val().trim();
        var $resultsContainer = $('#ajaxSearchResults-3 .resultsContent');
        $resultsContainer.empty();

        if (query.length >= 2) {
            debounceTimer = setTimeout(function() {
                console.log('Gửi yêu cầu tìm kiếm với từ khóa:', query);
                console.log('CSRF Token:', $('meta[name="_csrf"]').attr('content') || 'Không có token');
                $.ajax({
                    url: '/client/search',
                    type: 'GET',
                    data: { query: query },
                    headers: {
                        'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content') || ''
                    },
                    success: function(response) {
                        console.log('Phản hồi từ API:', response);
                        try {
                            if (response.results && response.results.length > 0) {
                                $('#ajaxSearchResults-3').addClass('active');
                                response.results.forEach(function(product) {
                                    console.log('Sản phẩm:', product);
                                    var proname = product.proname || 'Không có tên';
                                    var proimage = product.proimage || '/resources/images/default.jpg';
                                    var proprice = product.proprice != null ? product.proprice : 0;
                                    var prosale = product.prosale != null ? product.prosale : 0;
                                    // Tính giá giảm
                                    var discountedPrice = prosale > 0 ? Math.round(proprice * (1 - prosale / 100)) : proprice;
                                    // Định dạng giá với dấu phẩy
                                    var formattedPrice = discountedPrice.toLocaleString('vi-VN');
                                    var formattedOriginalPrice = proprice.toLocaleString('vi-VN');
                                    var productHtml = `
                                        <div class="item-ult">
                                            <div class="thumbs">
                                                <a href="/client/detail?id=${product.id}" title="${proname}">
                                                    <img alt="${proname}" src="/resources/uploads/${proimage}">
                                                </a>
                                            </div>
                                            <div class="title">
                                                <a href="/client/detail?id=${product.id}" title="${proname}">${proname}</a>
                                                <p class="f-initial">
                                                    ${formattedPrice}₫
                                                    ${prosale > 0 ? `<del>${formattedOriginalPrice}₫</del>` : ''}
                                                </p>
                                            </div>
                                        </div>`;
                                    $resultsContainer.append(productHtml);
                                });
                                $resultsContainer.append(`
                                    <div class="resultsMore">
                                        <a href="/client/searchresult?query=${encodeURIComponent(query)}">Xem thêm ${response.total} sản phẩm</a>
                                    </div>
                                `);
                            } else {
                                $('#ajaxSearchResults-3').addClass('active');
                                $resultsContainer.append('<div class="item-ult">Không tìm thấy sản phẩm nào.</div>');
                            }
                        } catch (e) {
                            console.error('Lỗi khi xử lý phản hồi:', e);
                            $('#ajaxSearchResults-3').addClass('active');
                            $resultsContainer.append('<div class="item-ult">Lỗi xử lý dữ liệu. Vui lòng thử lại.</div>');
                        }
                    },
                    error: function(xhr) {
                        console.error('Lỗi khi tìm kiếm:', xhr.status, xhr.responseText);
                        $('#ajaxSearchResults-3').addClass('active');
                        $resultsContainer.append('<div class="item-ult">Lỗi khi tìm kiếm. Vui lòng thử lại. (Mã lỗi: ' + xhr.status + ')</div>');
                    }
                });
            }, 300);
        } else {
            $('#ajaxSearchResults-3').removeClass('active');
        }
    });

    // Xử lý nút tìm kiếm
    $('#button-addon2').on('click', function() {
        var query = $('.input-search').val().trim();
        if (query.length > 0) {
            window.location.href = `/client/searchresult?query=${encodeURIComponent(query)}`;
        }
    });

    // Ẩn kết quả khi click ra ngoài
    $(document).on('click', function(e) {
        if (!$(e.target).closest('.search-container').length) {
            $('#ajaxSearchResults-3').removeClass('active');
        }
    });
});