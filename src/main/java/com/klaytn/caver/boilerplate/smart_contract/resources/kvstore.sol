pragma solidity ^0.5.6;

contract kvstore {
    event Set(address indexed from, string indexed key, string value);

    mapping(string=>string) kvStorage;
    function get(string memory key) public view returns (string memory) {
        return kvStorage[key];
    }
    function set(string memory key, string memory value) public {
        kvStorage[key] = value;
        emit Set(msg.sender, key, value);
    }
}
