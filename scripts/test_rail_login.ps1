$body = @{ 
    username = 'railtestuser2'
    password = 'RailPass123!'
} | ConvertTo-Json

try {
    $r = Invoke-RestMethod -Uri 'https://hmsback-production.up.railway.app/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -Headers @{ Origin = 'http://localhost:3000' } -ErrorAction Stop
    $r | ConvertTo-Json
} catch {
    Write-Host 'REQUEST FAILED'
    if ($_.Exception -and $_.Exception.Response) {
        $resp = $_.Exception.Response
        try { $status = $resp.StatusCode.Value__ } catch { $status = $resp.StatusCode }
        Write-Host "Status: $status"
        $stream = $resp.GetResponseStream()
        $sr = New-Object System.IO.StreamReader($stream)
        $bodyResp = $sr.ReadToEnd()
        Write-Host 'Response body:'
        Write-Host $bodyResp
    }
    Write-Host 'Error message:' $_.Exception.Message
}
