-- ARGV[1]=idpKey
local KEY_PREFIX = 'idp-'
local newKJson = ARGV[1]
local expire_seconds = ARGV[2]
local res = {}
local newK = cjson.decode(newKJson)
local oldK = redis.call("get", KEY_PREFIX .. newK.id)
-- 原key是否存在
if (false == oldK) then
    redis.call("set", KEY_PREFIX .. newK.id, newKJson)
    redis.call("expire", KEY_PREFIX .. newK.id, expire_seconds)
    res['idpKey'] = newK
    res['count'] = 1
else
    res['idpKey'] = oldK
    res['count'] = 0
end
return cjson.encode(res)