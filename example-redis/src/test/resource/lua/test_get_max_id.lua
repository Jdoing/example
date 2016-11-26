--
-- Created by IntelliJ IDEA.
-- User: jiangyu
-- Date: 16/8/18
-- Time: 10:33
-- To change this template use File | Settings | File Templates.
--

local function get_max_seq()
    local key = 'orderId'
    local seq = tostring(KEYS[1])
    local month_in_seconds = 24 * 60 * 60 * 30

    if (1 == redis.call('setnx', key, seq))
    then
        redis.call('expire', key, month_in_seconds)
        return tostring(seq)
    else
        local prev_seq = tostring(redis.call('get', key))
        if (prev_seq < seq)
        then
            redis.call('set', key, seq)
            return seq
        else
            redis.call('incr', key)
            return redis.call('get', key)
        end

        --        return tonumber(max_seq)
    end
end

return get_max_seq()
