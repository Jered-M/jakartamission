<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Convertisseur USD → IDR</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
            padding: 40px;
            max-width: 450px;
            width: 100%;
        }
        
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 10px;
            font-size: 32px;
        }
        
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 30px;
            font-size: 14px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            font-size: 14px;
        }
        
        input {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        input:focus {
            outline: none;
            border-color: #667eea;
            background: #f9f9f9;
        }
        
        button {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
        }
        
        button:active {
            transform: translateY(0);
        }
        
        .result-box {
            background: #f0f4ff;
            border-left: 4px solid #667eea;
            padding: 20px;
            border-radius: 8px;
            margin-top: 25px;
            display: none;
        }
        
        .result-box.show {
            display: block;
            animation: slideIn 0.3s ease-in;
        }
        
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .result-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .result-item:last-child {
            border-bottom: none;
        }
        
        .result-label {
            color: #666;
            font-weight: 500;
            font-size: 14px;
        }
        
        .result-value {
            font-weight: 700;
            color: #667eea;
            font-size: 18px;
        }
        
        .error-box {
            background: #f8d7da;
            border-left: 4px solid #dc3545;
            padding: 15px;
            border-radius: 8px;
            color: #721c24;
            margin-top: 20px;
            display: none;
        }
        
        .error-box.show {
            display: block;
        }
        
        .rate-info {
            background: #e8f5e9;
            border-left: 4px solid #4caf50;
            padding: 12px;
            border-radius: 8px;
            margin-top: 20px;
            font-size: 13px;
            color: #2e7d32;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>💱</h1>
    <h1>Convertisseur</h1>
    <p class="subtitle">Dollar US → Roupie Indonésienne</p>
    
    <form method="POST" action="convert" id="converterForm">
        <div class="form-group">
            <label for="usd">Montant (USD)</label>
            <input 
                type="number" 
                id="usd" 
                name="usd" 
                placeholder="Ex: 100" 
                step="0.01" 
                min="0" 
                required
            >
        </div>
        
        <button type="submit">Convertir</button>
    </form>
    
    <%
        String usd = (String) request.getAttribute("usd");
        String idr = (String) request.getAttribute("idr");
        String error = (String) request.getAttribute("error");
        Boolean success = (Boolean) request.getAttribute("success");
        
        if (success != null && success) {
    %>
    <div class="result-box show">
        <div class="result-item">
            <span class="result-label">Montant USD</span>
            <span class="result-value"><%= usd %> $</span>
        </div>
        <div class="result-item">
            <span class="result-label">Équivalent IDR</span>
            <span class="result-value"><%= idr %> Rp</span>
        </div>
    </div>
    
    <div class="rate-info">
        ℹ️ Taux de change: 1 USD = 15,800 IDR
    </div>
    <% } %>
    
    <% if (error != null) { %>
    <div class="error-box show">
        ❌ <%= error %>
    </div>
    <% } %>
</div>

<script>
    document.getElementById('converterForm').addEventListener('submit', function(e) {
        const usdValue = parseFloat(document.getElementById('usd').value);
        if (isNaN(usdValue) || usdValue <= 0) {
            e.preventDefault();
            alert('Veuillez entrer un montant valide');
        }
    });
</script>

</body>
</html>
