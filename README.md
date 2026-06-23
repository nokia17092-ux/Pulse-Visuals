# ✦ Pulse Visuals

**Minecraft 1.21.4 | Fabric Mod**

Визуальные PvP-эффекты, оптимизация FPS и кастомный HUD.

---

## 📦 Сборка

### Требования
- **Java 21** (JDK)
- **Gradle** (или используй `./gradlew`)

### Шаги

```bash
# 1. Войти в папку мода
cd pulse-visuals

# 2. Скачать Gradle Wrapper (один раз)
gradle wrapper --gradle-version=8.8

# 3. Собрать мод
./gradlew build

# JAR появится в:
# build/libs/pulse-visuals-1.0.0.jar
```

### Установка
1. Убедись, что установлен **Fabric Loader 0.16.x** для Minecraft 1.21.4
2. Скопируй JAR в папку `.minecraft/mods/`
3. Запусти игру

---

## 🎮 Управление

| Действие | Клавиша |
|---|---|
| Открыть меню Pulse Visuals | **Right CTRL** |
| Кнопка в паузе (ESC-меню) | **"✦ Visuals"** |

---

## 🎨 Возможности

### Hit Particles — Частицы удара
- Кастомные частицы при ударе
- 🟡 Золотые — обычный удар
- 🔴 Красные — критический удар

### Target HUD — HUD цели
- HP-бар цели с цветовой индикацией
- Уровень брони, ник, расстояние

### Damage Numbers — Числа урона
- 🟡 Золотой: обычный урон
- 🔴 Красный: критический
- 🟢 Зелёный: исцеление
- Плавное всплытие + затухание

### Critical Hit Effect — Эффект крита
- Красная вспышка по краям экрана

### Trajectory Prediction — Траектория снарядов
- Пунктирная линия для лука/арбалета/трезубца
- Учёт гравитации и сопротивления воздуха

### Weapon Trails — Следы оружия
- Золотой след при обычном ударе
- Красный след при критическом

### Combo Counter — Счётчик комбо
- Показывает серию ударов подряд
- Цвет меняется: золотой → оранжевый → красный (10+)

### Dynamic Crosshair — Динамический прицел
- Расширяется при спринте
- Краснеет при наведении на живые существа

### Low HP Pulse — Пульсация при низком HP
- Красная пульсирующая виньетка

### Hit Direction Indicator — Направление удара
- Показывает откуда пришёл урон

### Kill Feed — Лента убийств
- Лог убийств в углу экрана

### ⚡ FPS Optimizer
| Техника | Эффект |
|---|---|
| Entity Culling | +30–60 FPS: скрывает сущности за стенами |
| Smart Particle Limiter | +10–20 FPS: снижает частицы при просадках |
| Mob AI Throttle | +10–15 FPS: реже обновляет AI мобов |
| Dynamic Render Distance | +15–30 FPS: меньше дистанция при движении |

---

## 📁 Структура проекта

```
src/
├── main/java/com/pulsevisuals/
│   └── PulseVisuals.java              # Точка входа
├── client/java/com/pulsevisuals/
│   ├── PulseVisualsClient.java        # Клиентская инициализация
│   ├── config/PulseConfig.java        # Конфиг (JSON)
│   ├── gui/PulseVisualsScreen.java    # Меню настроек
│   ├── hud/                           # HUD элементы
│   │   ├── TargetHud.java
│   │   ├── DamageNumberManager.java
│   │   ├── ComboManager.java
│   │   ├── KillFeedManager.java
│   │   └── HitDirectionIndicator.java
│   ├── effects/                       # Визуальные эффекты
│   │   ├── HitParticleManager.java
│   │   ├── WeaponTrailRenderer.java
│   │   ├── SprintTrailManager.java
│   │   ├── TrajectoryRenderer.java
│   │   ├── CriticalHitEffect.java
│   │   ├── DynamicCrosshair.java
│   │   └── LowHpPulse.java
│   ├── optimizer/FpsOptimizer.java    # FPS оптимизатор
│   └── mixin/                         # Mixins
│       ├── LivingEntityMixin.java
│       ├── GameMenuScreenMixin.java
│       ├── InGameHudMixin.java
│       ├── GameRendererMixin.java
│       └── EntityRendererMixin.java
```

---

## ⚙️ Конфиг

Конфиг сохраняется в `.minecraft/config/pulse-visuals.json`
Автоматически сохраняется при закрытии меню.
