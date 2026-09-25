redis.call("RPUSH", KEYS[1], ARGV[1])

local queue_size = redis.call("LLEN", KEYS[1])

if queue_size == 2 then
    local playerOne = redis.call("LPOP", KEYS[1])
    local playerTwo = redis.call("LPOP", KEYS[1])

    return {playerOne, playerTwo}
end

return {}
