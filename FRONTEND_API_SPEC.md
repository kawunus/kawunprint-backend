````# 📤 API загрузки файлов - Спецификация для фронтенда

## 🎯 Основные лимиты
- **Максимум файлов на заказ:** 5
- **Максимальный размер файла:** 20MB
- **Разрешенные типы:** STL, OBJ, изображения, PDF, архивы

## 📡 Endpoints

### 1. Загрузить файл к заказу
```http
POST /api/v1/orders/{orderId}/files
Content-Type: multipart/form-data
Authorization: Bearer {token}
```

**Body:** `file` (multipart form data)

**Response 201:**
```json
{
  "id": 1,
  "orderId": 15,
  "fileName": "model.stl",
  "fileUrl": "https://storage.googleapis.com/...",
  "fileSize": 2048576,
  "mimeType": "application/octet-stream",
  "uploadedAt": "2025-11-19T10:30:00",
  "uploadedBy": 5
}
```

**Errors:**
- `400` - "Максимум 5 файлов на заказ"
- `400` - "Размер файла не должен превышать 20.00 MB"
- `400` - "Тип файла не поддерживается"
- `403` - "You can only upload files to your own orders"

### 2. Получить файлы заказа
```http
GET /api/v1/orders/{orderId}/files
Authorization: Bearer {token}
```

**Response 200:** Array of files (same format as upload response)

### 3. Статистика файлов заказа
```http
GET /api/v1/orders/{orderId}/files/stats
Authorization: Bearer {token}
```

**Response 200:**
```json
{
  "orderId": 15,
  "totalFiles": 3,
  "maxFiles": 5,
  "remainingSlots": 2,
  "totalSize": 52428800,
  "totalSizeFormatted": "50.00 MB",
  "canUploadMore": true,
  "files": [
    {
      "id": 1,
      "fileName": "model.stl",
      "size": 20971520,
      "sizeFormatted": "20.00 MB",
      "mimeType": "application/octet-stream",
      "isImage": false,
      "uploadedAt": "2025-11-19T15:30:00"
    }
  ]
}
```

### 4. Удалить файл
```http
DELETE /api/v1/orders/{orderId}/files/{fileId}
Authorization: Bearer {token}
```

**Response 200:** `{"message": "File deleted successfully"}`

## 🔐 Права доступа

| Роль | Загрузка | Просмотр | Удаление |
|------|----------|----------|----------|
| **CLIENT** | ✅ Свои заказы | ✅ Свои заказы | ✅ Свои файлы |
| **EMPLOYEE** | ✅ Все заказы | ✅ Все заказы | ✅ Все файлы |
| **ANALYST** | ❌ | ✅ Все заказы | ❌ |
| **ADMIN** | ✅ Все заказы | ✅ Все заказы | ✅ Все файлы |

## 🎨 Рекомендации для UI

### Компонент загрузки файлов
```tsx
function FileUpload({ orderId }) {
  // 1. Проверить статистику: GET /api/v1/orders/{orderId}/files/stats
  // 2. Показать: "Файлов: 3/5, Осталось: 2 слота"
  // 3. Drag & drop или file picker
  // 4. Валидировать размер и тип ПЕРЕД загрузкой
  // 5. Progress bar во время загрузки
  // 6. Обновить список после успешной загрузки
}
```

### Обработка ошибок
```tsx
const handleUpload = async (file) => {
  // Клиентская валидация
  if (file.size > 20 * 1024 * 1024) {
    showError("Файл слишком большой (макс 20MB)");
    return;
  }
  
  if (totalFiles >= 5) {
    showError("Максимум 5 файлов на заказ");
    return;
  }
  
  try {
    const response = await uploadFile(file);
    showSuccess("Файл загружен успешно");
    refreshFileList();
  } catch (error) {
    showError(error.message);
  }
};
```

### Список файлов
```tsx
function FileList({ files, canDelete }) {
  return files.map(file => (
    <div key={file.id}>
      <span>{file.fileName}</span>
      <span>{file.sizeFormatted}</span>
      {file.isImage && <img src={file.fileUrl} />}
      {canDelete && <DeleteButton fileId={file.id} />}
    </div>
  ));
}
```

## 🧪 Тестирование

### JavaScript пример
```javascript
// Загрузка файла
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const response = await fetch(`/api/v1/orders/${orderId}/files`, {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` },
  body: formData
});

const result = await response.json();
if (response.ok) {
  console.log('Файл загружен:', result);
} else {
  console.error('Ошибка:', result);
}
```

---

**🎯 Всё что нужно знать фронтенду для интеграции!**
````