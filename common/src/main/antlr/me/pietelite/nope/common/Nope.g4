grammar Nope;

nope: NOPE (evaluate | host | hosts | profile | profiles | reload | settings | tool | verbose | zones)?;
evaluate: EVALUATE setting=identifier subject=identifier?;

// nope host
host: HOST hostName=identifier (hostDestroy | hostEdit | hostInfo | hostShow);
hostDestroy: DESTROY;

// nope host edit
hostEdit: EDIT (hostEditName | hostEditPriority | hostEditProfiles | hostEditTarget | hostEditZones);
hostEditName: NAME name=identifier;
hostEditPriority: PRIORITY priority=identifier;
hostEditProfiles: PROFILES (insertProfile | removeProfile);
hostEditTarget: TARGET profileName=identifier target;

// nope host edit zones
hostEditZones: ZONES createZone | destroyZone | editZone;
createZone: CREATE (createZoneBuilder | createZoneExplicit | createZoneSelection);
createZoneBuilder: BUILDER BOX;
createZoneExplicit: EXPLICIT (createBox | createCylinder | createSlab | createSphere);
createBox: BOX domain=identifier x1=identifier y1=identifier z1=identifier x2=identifier y2=identifier z2=identifier;
createCylinder: CYLINDER domain=identifier x=identifier y1=identifier y2=identifier z=identifier radius=identifier;
createSlab: SLAB domain=identifier y1=identifier y2=identifier;
createSphere: SPHERE domain=identifier x=identifier y=identifier z=identifier radius=identifier;
createZoneSelection: SELECTION zone;
destroyZone: DESTROY index=identifier;
editZone: EDIT index=identifier;

// nope host edit profiles
insertProfile: INSERT profileName=identifier index=identifier;
removeProfile: REMOVE profileName=identifier;

hostInfo: INFO (PROFILES | ZONES)?;
hostShow: SHOW;

hosts: HOSTS (hostsCreate | LIST | LISTALL);
hostsCreate: CREATE hostName=identifier;

// nope profile
profile: PROFILE profileName=identifier (profileDestroy | profileEdit | profileEditor | profileInfo);
profileDestroy: DESTROY;

// nope profile edit
profileEdit: EDIT (profileEditClear | profileEditName | profileEditSetting | target);
profileEditClear: CLEAR;
profileEditName: NAME name=identifier;
profileEditSetting: SETTING profileEditSingleSetting | profileEditMultiSetting;
profileEditSingleSetting: SINGLE setting=identifier (target | singleSettingValue);
allSettingValue: UNSET | SETDEFAULT;
singleSettingValue: VALUE (allSettingValue | singleSet);
singleSet: SET ID;
profileEditMultiSetting: MULTI setting=identifier (target | multiSettingValue);
multiSettingValue: VALUE (DECLARATIVE | ADDITIVE | SUBTRACTIVE) (allSettingValue | multiAdd | multiRemove | multiSet | multiSetAll | multiSetNone | multiSetNot);
multiAdd: ADD ID_SET;
multiRemove: REMOVE ID_SET;
multiSet: SET ID_SET;
multiSetAll: SETALL ID_SET;
multiSetNone: SETNONE;
multiSetNot: SETNOT ID_SET;
profileEditor: EDITOR;
profileInfo: INFO;

profiles: PROFILES (profilesCreate | LIST);
profilesCreate: CREATE profileName=identifier;

reload: RELOAD;
settings: SETTINGS category=identifier?;
tool: TOOL zone;
verbose: VERBOSE (OFF | ON);
zones: ZONES (APPLY | STOP);

// targets
target: targetForce | targetPermission | targetSubject | targetSet | targetToggle;
targetForce: FORCE;
targetPermission: PERMISSION (targetPermissionAdd | targetPermissionClear | targetPermissionRemove);
targetPermissionAdd: ADD permission=identifier value=identifier;
targetPermissionClear: CLEAR;
targetPermissionRemove: REMOVE permission=identifier;
targetSubject: SUBJECT (targetSubjectAdd | targetSubjectClear | targetSubjectRemove);
targetSubjectAdd: ADD subject=identifier;
targetSubjectClear: CLEAR;
targetSubjectRemove: REMOVE subject=identifier;
targetSet: SETALL | SETNONE;
targetToggle: TOGGLE;

// zone types
zone: BOX | CYLINDER | SLAB | SPHERE;

ADD: 'add';
ADDITIVE: 'additive';
APPLY: 'apply';
BOX: 'box';
BUILDER: 'builder';
CLEAR: 'clear';
CREATE: 'create';
CYLINDER: 'cylinder';
DECLARATIVE: 'declarative';
DESTROY: 'destroy';
EDIT: 'edit';
EDITOR: 'editor';
EVALUATE: 'evaluate';
EXPLICIT: 'explicit';
FORCE: 'force';
HOST: 'host';
HOSTS: 'hosts';
INFO: 'info';
INSERT: 'insert';
LIST: 'list';
LISTALL: 'listall';
MULTI: 'multi';
NAME: 'name';
NOPE: 'nope';
OFF: 'off';
ON: 'on';
PERMISSION: 'permission';
PRIORITY: 'priority';
PROFILE: 'profile';
PROFILES: 'profiles';
RELOAD: 'reload';
REMOVE: 'remove';
SELECTION: 'selection';
SET: 'set';
SETALL: 'setall';
SETDEFAULT: 'setdefault';
SETNONE: 'setnone';
SETNOT: 'setnot';
SETTING: 'setting';
SETTINGS: 'settings';
SINGLE: 'single';
SLAB: 'slab';
SPHERE: 'sphere';
STOP: 'stop';
SUBJECT: 'player';
SUBTRACTIVE: 'subtractive';
SHOW: 'show';
TARGET: 'target';
TOGGLE: 'toggle';
TOOL: 'tool';
UNSET: 'unset';
VALUE: 'value';
VERBOSE: 'verbose';
ZONES: 'zones';

// MANTLE NODES
identifier: ID | SINGLE_QUOTE ID SINGLE_QUOTE | DOUBLE_QUOTE ID DOUBLE_QUOTE;
ID: [a-zA-Z0-9\-_]+;
ID_SET: ID (COMMA ID)+;
COMMA: ',';
SINGLE_QUOTE: '\'';
DOUBLE_QUOTE: '"';
WS : [ \t\r\n]+ -> channel(HIDDEN); // skip spaces, tabs, newlines
// END MANTLE NODES