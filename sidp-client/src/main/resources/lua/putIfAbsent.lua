-- ARGV[1]=idpKey
local KEY_PREFIX = 'idp-'
local newKJson = ARGV[1]
local newK = cjson.decode(newKJson)
local oldK = redis.call("get", KEY_PREFIX .. newK.id)
local res = {}
if (false == oldK) then
    redis.call("set", KEY_PREFIX .. newK.id, newKJson)
    res['idpKey'] = newK
    res['count'] = 1
else
    res['idpKey'] = oldK
    res['count'] = 0
end
return cjson.encode(res)