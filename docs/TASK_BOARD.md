# LAST ROUTE — TASK BOARD

**Nhóm:** Đức (Leader) · Long · Phú  
**Repository:** [Last_Route_forDSA](https://github.com/gaconcute1411/Last_Route_forDSA)  
**Baseline:** `main` — `515acd8`
**Trạng thái:** Kế hoạch phân công; cập nhật tiến độ thực tế bằng GitHub Issues/Projects.

## 1. Mục tiêu và code hiện có

Từ bản Java console hiện tại, xây dựng **một mission chơi được**:

`Hospital → [Store: nhặt item] → School: cứu 1 survivor → Ambush → Stadium: Safe Zone`

- Sau rescue: `School–Stadium` chuyển **BLOCKED**, `School–Highway` chuyển **INFESTED**; game tính lại route. Road BLOCKED không thể đi qua.
- BFS tìm đường **ít cạnh**; DFS duyệt/đánh giá các route **ít zombie** trên map nhỏ.
- Đi qua đường có zombie sẽ mở Combat View: zombie di chuyển, tấn công, phản ứng với Noise; thắng khi **player và survivor còn sống** đến Stadium.
- **Đã có:** `Graph`, `Node`, `Edge`, `RoadStatus`, `BFS`, `RouteStrategy`, `SampleMaps` (13 locations, 22 roads), `Main` chạy console.
- **Chưa có:** Dynamic Road, DFS, GameState, Items, Combat, GUI, tests/build chuẩn. **Giữ `SampleMaps.createCity()` để kiểm thử hồi quy.**

## 2. Phân công chính

| Thành viên | Phụ trách | Module chính |
|---|---|---|
| **Đức (Leader)** | Core Architecture, GameState, Map UI, Integration, review/merge | `graph/Graph.java`, `model/Road.java`, `game/*`, `ui/CityMapPanel.java`, `Main.java` |
| **Long** | BFS/DFS, RoutePlanner, Inventory/Items, Sorting | `graph/BFS.java`, `graph/DFS.java`, `graph/RoutePlanner.java`, `item/*`, `ui/InventoryPanel.java` |
| **Phú** | Zombie models, CombatEngine, AI, Noise, Combat UI | `model/Zombie*.java`, `combat/*`, `ui/CombatPanel.java` |

> Mỗi module có một người chịu trách nhiệm chính. Thống nhất public API trước khi thay đổi module của nhau.

## 3. Danh sách nhiệm vụ (16 tasks)

**Trạng thái:** `TODO` → `IN PROGRESS` → `IN REVIEW` → `DONE`; dùng `BLOCKED` nếu có phụ thuộc chưa giải quyết.

### Sprint 0 — Foundation

| ID | Người | Task / kết quả cần có | Phụ thuộc | Status |
|---|---|---|---|---|
| D-01 | Đức | Chốt JDK, build/test và API contracts (`README`, `docs/ARCHITECTURE_CONTRACTS.md`) | — | TODO |
| D-02 | Đức | Shared mutable `Road`, cập nhật hai chiều, `graphVersion` và tests | D-01 | TODO |
| L-01 | Long | BFS regression tests; DFS traversal + connectivity | API Graph | TODO |
| P-01 | Phú | `Zombie`, `ZombieType`, `CombatEncounter`, combat contracts | D-01 | TODO |

**Mốc M0:** Build chạy; Sample Map cũ không hỏng; thay đổi road đồng bộ hai chiều; 3 người thống nhất API.

### Sprint 1 — Core Gameplay Logic

| ID | Người | Task / kết quả cần có | Phụ thuộc | Status |
|---|---|---|---|---|
| D-03 | Đức | `GameState`, `Player`, 1 `Survivor`, rescue/ambush event | D-02 | TODO |
| L-02 | Long | DFS simple paths, risk score, BFS vs safest route, tests | D-02, L-01 | TODO |
| L-03 | Long | Item/Inventory, seeded random, pickup một lần, tests | GameState API | TODO |
| P-02 | Phú | `CombatEngine`: HP, damage, ammo, Walker/Runner/Tank, tests | P-01, Inventory API | TODO |

**Mốc M1:** Có thể kiểm thử Graph, route, item pickup, combat độc lập, chưa cần GUI.

### Sprint 2 — Playable Game & UI

| ID | Người | Task / kết quả cần có | Phụ thuộc | Status |
|---|---|---|---|---|
| D-04 | Đức | Java Swing/Java2D City Map + ambush warning | D-03, L-02 | TODO |
| D-05 | Đức | `GameEngine`: move, rescue, encounter, win/lose | D-03, L-02, L-03, P-02 | TODO |
| L-04 | Long | Supply Route, Insertion Sort, Inventory UI, route overlay | L-02, L-03, Map UI API | TODO |
| P-03 | Phú | Zombie chase/attack, Noise, finite waves, NORMAL difficulty | P-02 | TODO |
| P-04 | Phú | Combat View 2.5D, controls, HUD/zombie counter | P-02, P-03 | TODO |

**Mốc M2:** Đi được từ Map View đến rescue, đường thay đổi, gặp zombie và chiến đấu trở lại Map View.

### Sprint 3 — Integration & Documentation

| ID | Người | Task / kết quả cần có | Phụ thuộc | Status |
|---|---|---|---|---|
| D-06 | Đức | Tích hợp end-to-end, build/run, README, win/lose tests, review PR | M2 | TODO |
| L-05 | Long | Route/Item tests, giải thích DSA + Big-O | L-04 | TODO |
| P-05 | Phú | Combat tests, cân bằng NORMAL, pause/noise/HP/counter | P-04 | TODO |

**Mốc M3:** Clone mới → build → test → chạy trọn một mission, không crash.

## 4. Quy ước tích hợp bắt buộc

- **Đức:** `Graph.updateRoad(...)` cập nhật trạng thái đường hai chiều; `GameEngine` quyết định state/game flow.
- **Long:** `RoutePlanner` đọc Graph nhưng **không sửa Graph**; BFS chỉ tối ưu số cạnh; DFS đánh giá risk trên map nhỏ.
- **Phú:** `CombatEngine` quản lý HP, zombie còn sống, damage và Noise; UI chỉ hiển thị state.
- Route phải được kiểm tra lại sau ambush; INFESTED đi được nhưng cần combat, BLOCKED không đi được.
- Item dùng seeded random để test tái lập; zombie waves hữu hạn; rescue chỉ trigger một lần.

## 5. GitHub workflow & Definition of Done

1. Tạo **1 GitHub Issue cho mỗi Task ID**; gắn assignee, Sprint và dependency. Dùng GitHub Projects theo dõi status.
2. Làm trên branch riêng, ví dụ: `feature/D-02-shared-road`, `feature/L-02-route-planner`, `feature/P-03-zombie-ai`.
3. Mở Pull Request vào `main`, ghi `Closes #<issue-number>`; **không push trực tiếp lên `main`**.
4. PR được ít nhất 1 người khác review; PR của Đức do Long hoặc Phú review. Đức phụ trách merge và giữ `main` build được.
5. Chỉ đánh dấu **DONE** khi code đã merge, test/build thực sự pass, API tương thích và Issue/Project đã cập nhật.

**Smoke tests cuối dự án:** Map vẫn có 13 nodes/22 roads; BFS cũ không lỗi; road đổi hai chiều; rescue chỉ xảy ra một lần; không đi qua BLOCKED; item nhặt một lần; zombie chase/attack + Noise đúng; counter giảm khi zombie chết; pause dừng combat; chỉ thắng khi cả hai về Stadium an toàn.
