# 0.2.0
- added settings
  - added `armor-stand-place` setting
  - added `armor-stand-interact` setting
  - added `item-frame-place`setting
  - added `item-frame-interact` setting
  - added `painting-place` setting
  - included the above to the `malicious-protections` template
  - added `leash` setting
- added block-cache size to `/nope` for debugging
- removed `interact` setting from the `malicious-protections` template
- `/nope reload` loads a new host tree in memory and swaps it with the 
  plugin's tree to ensure no concurrency problems 
- `/nope reload` now registers any new necessary listeners
- updated `/nope info` formatting to include redundant setting highlighting and clickable commands
- fixed `ride` setting
- `player-collision` denial now stops fishhooks from hooking players
- stopped block propagation to tnt when tnt-ignition is denied to deny
  activation with redstone
- made spawn-mob settings allow-deny instead of true-false
- ensured that any movement of any combination of riding-entities is handled
  correctly within movement settings
- stops from sending spamming duplicate messages for greeting/farewell messages
  but still sends quick subsequent distinct messages
- setting the priority of a zone with the same priority as another intersecting
  zone will bump other priorities higher
- creates a backup file every time the server closes
- bug fixes
