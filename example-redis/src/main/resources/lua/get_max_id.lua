--
-- Created by IntelliJ IDEA.
-- User: jiangyu
-- Date: 16/8/18
-- Time: 09:53
-- To change this template use File | Settings | File Templates.
--

local curr_seq
local prev_seq=redis.call('get', 'orderId')
print(prev_seq)
if(prev_seq == false)
then
    prev_seq = 'date' .. "99999"
    print(prev_seq)
else
    print('b')
end
