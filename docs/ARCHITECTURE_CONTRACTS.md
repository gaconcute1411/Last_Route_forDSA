# LAST ROUTE - ARCHITECTURE CONTRACTS
**Nhóm:** Đức (Leader) · Long · Phú  
**Repository:** [Last_Route_forDSA](https://github.com/gaconcute1411/Last_Route_forDSA)  
**Trạng thái:** Đề xuất để nhóm thống nhất trước Sprint 0. API mới **chưa được implement**.  
**Liên quan:** [TASK_BOARD.md](TASK_BOARD.md)

## 1. Mục đích và nguyên tắc

File này quy định cách các module Java trao đổi dữ liệu để ba thành viên làm song song, không phải thiết kế lại toàn bộ source.

- **Đức:** Graph, shared Road, GameState, GameEngine, Map UI và tích hợp.
- **Long:** BFS/DFS, RoutePlanner, RouteEvaluator, Item/Inventory và Inventory UI.
- **Phú:** Zombie model, CombatEngine, Zombie AI, Noise System và Combat UI.
- **Chỉ một nơi quyết định game state:** `GameEngine` điều phối; Graph lưu tình trạng road; Inventory lưu items; CombatEngine quản lý trạng thái encounter.
- **UI không chứa gameplay rules.** UI nhận state/snapshot rồi hiển thị, gửi action cho engine.
- **Không đổi public API của module khác khi chưa được cả nhóm thống nhất qua Issue/PR.**

## 2. Baseline đang có và quy ước build

**Hiện có trên `main`:** `Graph`, `Node`, `Edge`, `RoadStatus`, `BFS`, `RouteStrategy`, `SampleMaps`, `Main` (console). `Graph` lưu adjacency list; `BFS.findRoute(...)` trả `LinkedList<Node>`; `Node` có tọa độ `x/y`.

**Chưa có:** `Road` dùng chung, DFS, RoutePlanner, GameState, Inventory, CombatEngine, GUI, tests và build tool.

**Quyết định D-01 cần chốt trước khi code:** JDK (đề xuất Java 17), build/test tool (Maven + JUnit 5 hoặc cách khác), cấu trúc package và cách chạy test. Hiện repository dùng `src/graph`, `src/model`; **không tự chuyển sang `src/main/java`** khi chưa có quyết định chung.

Các API bên dưới là **contract dự kiến**, không phải method hiện hữu. Khi chốt Sprint 0, chỉ sửa tài liệu này qua PR nếu cần đổi signature.

## 3. Module ownership & boundary

| Owner | Module / package | Được phép làm | Không được tự ý làm |
|---|---|---|---|
| **Đức** | `graph/Graph`, `model/Road`, `game/*`, `ui/CityMapPanel`, `Main` | Cập nhật Graph, quản lý mission/game flow, chuyển màn hình | Sửa logic DFS/Inventory/Combat thay owner |
| **Long** | `graph/BFS`, `graph/DFS`, `graph/RoutePlanner`, `item/*`, `ui/InventoryPanel` | Đọc Graph, tính route, quản lý item | Sửa RoadStatus hay vị trí player trực tiếp từ RoutePlanner |
| **Phú** | `model/Zombie*`, `combat/*`, `ui/CombatPanel` | Quản lý encounter, zombie HP/AI/noise, combat HUD | Đổi topology Graph, tự quyết định rescue/win/lose toàn mission |

`model/Player`, `model/Survivor`, `model/GamePhase` và các dữ liệu giao tiếp giữa modules do **Đức chốt schema**, Long/Phú góp ý trước khi khóa contract.

## 4. Graph và Shared Road (Đức: D-02)

Giữ nguyên các API đã có để không phá code cũ:

```java
Graph.addNode(String name);
Graph.addNode(String name, int x, int y);
Graph.addRoad(String from, String to, RoadStatus status, int distance);
Graph.getNode(String name);
Graph.getNeighbors(String name);
BFS.findRoute(Graph graph, String from, String to, RouteStrategy strategy);
SampleMaps.createCity();
```

**API mới đề xuất:**

```java
Road Graph.getRoad(String from, String to);
void Graph.updateRoad(String from, String to, RoadStatus status, int zombieCount);
long Graph.getVersion();
```

**Quy tắc:**

1. Một road hai chiều có **một `Road` canonical** được hai `Edge` tham chiếu; cập nhật từ chiều nào cũng cho cùng kết quả.
2. `Road` lưu hai endpoint, `distance`, `RoadStatus`, `zombieCount >= 0`; chỉ `Graph` được phép điều phối mutation road.
3. `Graph.updateRoad(...)` cập nhật đồng thời status + zombie count và tăng `graphVersion` **một lần khi dữ liệu thực sự đổi**.
4. `BLOCKED` không thể đi qua; `INFESTED` vẫn đi được nhưng có thể kích hoạt combat.
5. Không thêm node/road trùng, không trả adjacency list có thể sửa tùy ý từ bên ngoài.
6. Route đã tạo ở version cũ phải được **kiểm tra lại ngay trước mỗi bước di chuyển**.
7. Giữ `SampleMaps.createCity()` nguyên hành vi (13 locations, 22 roads); thêm `SampleMaps.createRescueDemo()` cho mission mới, không sửa dữ liệu mẫu cũ.

**BFS cũ:** `SAFE` = chỉ SAFE; `DIRECT` = SAFE + DANGEROUS; `EMERGENCY` = mọi road trừ BLOCKED. Nếu nhóm muốn đổi ý nghĩa chiến lược, phải tạo Issue/PR riêng, không đổi ngầm.

## 5. RoutePlanner và Inventory (Long: L-01 đến L-04)

### Route API (đề xuất)

```java
RouteResult RoutePlanner.plan(Graph graph, String from, String to, RouteObjective objective);
RouteResult RoutePlanner.planViaSupply(Graph graph, String from, String supply, String to);
Set<String> DFS.reachableNodes(Graph graph, String from);
```

`RouteObjective` (mới): `FEWEST_EDGES`, `LOWEST_ZOMBIES`. Enum này mô tả **mục tiêu tối ưu**, khác với `RouteStrategy` cũ mô tả **chính sách được phép đi đường nào**. `RouteResult` nên là kết quả chỉ đọc, gồm:

| Field | Ý nghĩa |
|---|---|
| `List<String> nodeIds` | Danh sách vị trí theo thứ tự, gồm điểm đầu và đích |
| `long graphVersion` | Version Graph lúc tính route |
| `int hops` | Số road (`nodeIds.size() - 1` nếu có route) |
| `int estimatedZombies` | Tổng zombie dự kiến trên route |
| `int totalDistance` | Tổng distance từ Edge/Road |
| `boolean reachable` | Có tồn tại route hợp lệ hay không |

- `FEWEST_EDGES` sử dụng **BFS**: tìm ít road nhất trong các road có thể đi (`status != BLOCKED`); không có nghĩa là ít zombie nhất. Giữ nguyên `RouteStrategy.DIRECT` của BFS cũ (không cho INFESTED) để không gây regression.
- `LOWEST_ZOMBIES` dùng **DFS + recursion** liệt kê các *simple paths* trên map nhỏ, so sánh `estimatedZombies`, sau đó `hops`, rồi thứ tự location ID để kết quả ổn định. DFS exhaustive có thể tăng theo cấp số mũ; giới hạn quy mô và công khai giới hạn search nếu có.
- `planViaSupply` xét tối đa **một điểm dừng tùy chọn**; không được tự động nhặt vật phẩm hoặc di chuyển player.
- `RoutePlanner` **chỉ đọc Graph**, không gọi `updateRoad`, không đổi `GameState`.
- Route không tìm thấy trả kết quả `reachable=false`, `nodeIds=[]`; không trả route chứa road `BLOCKED`.

### Inventory API (đề xuất)

```java
boolean Inventory.add(Item item);
boolean Inventory.use(ItemType type);
int Inventory.count(ItemType type);
List<Item> Inventory.getItems();
```

- `ItemSpawner` dùng `Random(seed)` để kết quả nhặt đồ có thể test lặp lại.
- Mỗi pickup location chỉ nhận item **một lần** cho mỗi lượt chơi; `GameEngine` quyết định lúc nào player được pickup.
- `getItems()` trả danh sách chỉ đọc hoặc bản sao, tránh UI tự thay đổi dữ liệu.
- Súng, đạn, dao, bom, thuốc, huyết thanh là item; **Vehicle** nên quản lý như trạng thái phương tiện, không coi là vũ khí trong backpack.
- Long chốt với Phú contract sử dụng đạn/bom trước khi viết CombatEngine.

## 6. GameState / GameEngine (Đức: D-03, D-05)

**Trạng thái chính:** `PREPARING` → `ESCORTING` → `COMBAT` → `WON` / `LOST` (có thể thêm trạng thái nếu được thống nhất).

`GameState` chứa ít nhất: `Graph`, vị trí player, HP/infection, **đúng một** Survivor và trạng thái rescued/alive, Inventory, mission phase, active encounter (nếu có), current route/version. Dữ liệu state không được nhân bản và sửa độc lập giữa các màn hình.

**Các action tối thiểu (signature cụ thể chốt ở D-01):**

| Action | Input | Trách nhiệm GameEngine |
|---|---|---|
| `move(nextLocation)` | Node đích kề | Kiểm tra road trên Graph hiện tại; nếu có zombie thì mở encounter; chỉ cập nhật location sau khi thắng |
| `pickup()` | Location hiện tại | Gọi Inventory qua API; chặn pickup lại cùng vị trí |
| `rescue()` | Player tại School | Đánh dấu survivor được cứu và kích hoạt ambush **một lần** |
| `combatAction(action)` | Bắn/dao/bom/heal/... | Chuyển action cho CombatEngine, đồng bộ tài nguyên qua contract |
| `finishEncounter(result)` | Kết quả combat | Áp dụng damage/tiêu hao, tiếp tục di chuyển hoặc chuyển LOST |

**Mission demo:** Hospital (spawn) → Store (optional) → School (1 survivor) → Stadium (Safe Zone). Bản `createRescueDemo()` nên khởi tạo `School–Highway = DANGEROUS` (khác `createCity()` gốc đang là INFESTED). Sau khi cứu: `School–Stadium = BLOCKED`, `School–Highway = INFESTED`; `School–Airport` vẫn BLOCKED. Đảm bảo vẫn có ít nhất một đường thoát không BLOCKED.

**Win:** Player và survivor đều còn sống, survivor đã được cứu, và cả hai đến Stadium. Không tự động thắng chỉ vì player đến Safe Zone.

## 7. CombatEngine / Zombie AI (Phú: P-01 đến P-04)

Để tránh phụ thuộc UI, combat phải có logic thuần Java:

```java
CombatEncounter CombatEngine.startEncounter(EncounterRequest request);
CombatSnapshot CombatEngine.tick(CombatEncounter encounter, long deltaMs);
CombatSnapshot CombatEngine.applyAction(CombatEncounter encounter, CombatAction action);
CombatResult CombatEngine.finish(CombatEncounter encounter);
```

Đây là **interface đề xuất**; `EncounterRequest`, `CombatAction`, `CombatSnapshot`, `CombatResult` là các DTO/enum cần Phú và Đức thống nhất, chưa tồn tại trong repo.

- `EncounterRequest` phải mang: số zombie từ road, cấp độ chặng rút lui, seed, player stats và dữ liệu trang bị cần thiết (snapshot hoặc interface được thống nhất).
- `CombatSnapshot`: player HP, zombie còn sống, zombie positions/directions/HP, Noise, ammo, trạng thái combat — chỉ đọc từ UI.
- `CombatResult`: thắng/thua, damage hoặc HP còn lại, tài nguyên tiêu hao và các thay đổi cần GameEngine áp dụng **đúng một lần**.
- Mỗi zombie có ID riêng, loại `Walker / Runner / Tank`, max HP/current HP, damage, speed, trạng thái `IDLE / CHASE / ATTACK / DEAD`.
- Zombie **tiến về phía player theo thời gian thực**, đánh khi đủ tầm + hết cooldown; zombie chết không được tấn công.
- Gun/Bomb tạo `Noise`, làm zombie đang chờ vào trận sớm hơn; **không spawn vô hạn**. Counter là số zombie đang sống thực tế (kể cả zombie chưa xuất hiện nếu contract chọn tổng số còn lại), định nghĩa rõ trên HUD.
- Cấu hình `NORMAL` là data/config có thể chỉnh, **không hardcode rải rác trong UI**.
- `CombatPanel` (Java2D/2.5D) chỉ render `CombatSnapshot` và gửi input; không tự trừ HP, trừ đạn, thay zombie count.
- `tick(..., deltaMs)` cần quy tắc pause: **không tiến thời gian game hay gây damage khi pause**.

## 8. Luồng tích hợp giữa ba người

```text
CityMapPanel / InventoryPanel
          |
          v
      GameEngine  <---- RoutePlanner (Long; read-only Graph)
       |      |
       |      +---- Inventory (Long)
       |
       +---- Graph / GameState (Đức)
       |
       +---- CombatEngine (Phú) ----> CombatSnapshot --> CombatPanel
```

1. **Đức** cập nhật Graph và công bố public API trong PR `D-02`; Long dùng API đó để tính đường.
2. **Long** trả `RouteResult`; Đức kiểm tra `graphVersion` + từng road trước khi đi, không giả định route cũ luôn hợp lệ.
3. **Đức** vào road zombie → tạo `EncounterRequest` cho Phú; `CombatEngine` xử lý và trả snapshot/result.
4. **Đức** chỉ hoàn tất việc di chuyển khi combat kết thúc thành công; nếu thua thì mission LOST.
5. **UI** không được gọi trực tiếp `Graph.updateRoad()` hay sửa HP/zombie/item.

## 9. Quy tắc thay đổi API và kiểm thử

**Khi muốn đổi API:** tạo Issue/đề cập trong PR → ghi rõ class/method cũ và mới → Đức, Long, Phú đồng ý → cập nhật file này **trong cùng PR** với code/test liên quan. Tránh cùng sửa file sở hữu bởi người khác ở nhiều branch.

**Tối thiểu phải test:**

- Sample map gốc: 13 nodes, 22 roads; BFS vẫn chạy.
- Road cập nhật trạng thái hai chiều; BLOCKED bị loại khỏi mọi route.
- Rescue chỉ trigger một lần; return route được tính lại sau ambush.
- DFS reachable/risk có route an toàn hơn BFS trong case được thiết kế.
- Cùng seed tạo cùng item layout; pickup không trùng.
- Combat zombie chase/attack, Noise, pause; counter giảm đúng khi zombie chết.
- Player/survivor chết không thể thắng; clone mới có thể build/test/run.

**Definition of Done:** Code + test pass thật → PR được người khác review → merge vào `main` → cập nhật Issue/TASK_BOARD nếu có thay đổi phạm vi.

---

**Nhóm cần xác nhận trước khi bắt đầu Sprint 0:** JDK/build tool; signatures/DTO cuối cùng; ai sở hữu cách tiêu hao ammo/items trong combat; cách biểu diễn `CombatSnapshot` và quy tắc đếm zombie trên HUD.
