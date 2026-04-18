-- Plan-06 Task 23: Hide/disable deprecated order pages from menu
-- jst_order_main (3159) and jst_refund_record (3093) are already hidden (visible='1')
-- This script explicitly marks them as disabled (status='1') and ensures they are hidden
SET NAMES utf8mb4;

UPDATE sys_menu
SET visible = '1',
    status  = '1'
WHERE component IN (
    'jst/order/jst_order_main/index',
    'jst/order/jst_enroll_record/index',
    'jst/order/jst_refund_record/index'
);
